package pl.lodz.p.it.ports.view;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public interface JwtProviderPort {
    String generateJWT(String username, String role);

    Jws<Claims> parseJWT(String jwt);

    boolean validateToken(String jwt);

    String getToken(HttpServletRequest request);
}
