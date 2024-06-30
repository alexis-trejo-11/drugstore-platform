package microservice.user_service.Middleware;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    // Secret key for signing and verifying JWT tokens
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token validity duration (e.g., 1 hour)
    private final long validityDuration = 3600000;

    /**
     * Generates a JWT token with given user ID and roles.
     *
     * @param id    the user ID
     * @param roles the user roles
     * @return the generated JWT token
     */
    public String GenerateToken(Long id, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(id));
        claims.put("roles", roles);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityDuration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Validates a JWT token and extracts claims.
     *
     * @param token the JWT token to validate
     * @return the claims if the token is valid, otherwise null
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            // Invalid signature/claims
            return null;
        }
    }

    /**
     * Extracts user ID from the claims.
     *
     * @param claims the JWT claims
     * @return the user ID
     */
    public Long getUserId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Extracts roles from the claims.
     *
     * @param claims the JWT claims
     * @return the user roles
     */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }
}
