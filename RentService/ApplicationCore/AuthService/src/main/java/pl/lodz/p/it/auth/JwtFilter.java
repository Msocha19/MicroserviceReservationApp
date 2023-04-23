package pl.lodz.p.it.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = jwtProvider.getToken(request);
        try {
            if (jwt == null || !jwtProvider.validateToken(jwt)) {
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            null,
                            null,
                            Collections.singleton(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                filterChain.doFilter(request, response);
                return;
            }
        } catch (ExpiredJwtException exp) {
            response.setStatus(401);
            return;
        }

        Claims claims = jwtProvider.parseJWT(jwt).getBody();
        String authority = claims.get("role").toString();
        if (authority == null) {
            filterChain.doFilter(request, response);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                null,
                null,
            Collections.singleton(new SimpleGrantedAuthority("ROLE_" + authority)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
