package pl.lodz.p.it.auth;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.ports.view.JwtProviderPort;

import java.util.Date;

@Service
public class JwtProvider implements JwtProviderPort {

    private final String secret = "f4h9t87t3g473HGufuJ8fFHU4j39j48fmu948cx48cu2j9fjgrhdfgfd";

    @Override
    public String generateJWT(String username, String role) {
        long jwtExpirationInMillis = 9000000L;
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMillis))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    @Override
    public Jws<Claims> parseJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt);
    }

    @Override
    public boolean validateToken(String jwt) {
        try {
            parseJWT(jwt);
            return true;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "");
        }
        return null;
    }
}
