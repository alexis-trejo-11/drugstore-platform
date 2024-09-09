package at.backend.drugstore.microservice.common_classes.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Component
public class AuthSecurity {

    // Secret key for signing and verifying JWT tokens
    private final SecretKey secretKey;

    // Constructor to inject the secret key
    public AuthSecurity(@Value("${jwt.secret.key}") String secretKey) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

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
        Object clientId = claims.get("clientId");

        if (clientId instanceof Integer) {
            return ((Integer) clientId).longValue(); // Convierte Integer a Long
        } else if (clientId instanceof Long) {
            return (Long) clientId; // Ya es Long, simplemente lo devolvemos
        } else {
            throw new IllegalArgumentException("clientId is not of expected type Long or Integer");
        }
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
