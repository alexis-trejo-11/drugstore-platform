package at.backend.drugstore.microservice.common_classes.Middleware;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Component
public class AuthSecurity {

    // Secret key for signing and verifying JWT tokens
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(Long id, List<String> roles, Long clientId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(id));
        claims.put("roles", roles);
        claims.put("clientId", clientId); // Add clientId to claims

        Date now = new Date();
        // Token validity duration (e.g., 1 hour)
        long validityDuration = 3600000;
        Date validity = new Date(now.getTime() + validityDuration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)
                .compact();
    }

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

    public Long getUserId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }

    public Long getClientId(Claims claims) {
        return (Long) claims.get("clientId");
    }

    public Claims getClaimsFromToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7); // Remove "Bearer " prefix
            return validateToken(token);
        }
        return null;
    }

    public Long getClientIdFromToken(HttpServletRequest request) {
        Claims claims = getClaimsFromToken(request);
        if (claims != null) {
            return getClientId(claims);
        }
        return null;
    }

    public Long getUserIdFromToken(HttpServletRequest request) {
        Claims claims = getClaimsFromToken(request);
        if (claims != null) {
            return getUserId(claims);
        }
        return null;
    }

    public List<String> getRolesFromToken(HttpServletRequest request) {
        Claims claims = getClaimsFromToken(request);
        if (claims != null) {
            return getRoles(claims);
        }
        return null;
    }
}
