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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.lodz.p.it.domain.model.Customer;
import pl.lodz.p.it.domain.model.CustomerType;
import pl.lodz.p.it.domain.model.User;
import pl.lodz.p.it.services.UserService;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserService userDetailsService;


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
        User user;

        try {
            user = userDetailsService.loadUserByUsername(claims.getSubject());
        } catch (UsernameNotFoundException enfe) {
            filterChain.doFilter(request, response);
            return;
        }

        if (user.getType() == CustomerType.CUSTOMER) {
            if (!((Customer) user).isActive()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
