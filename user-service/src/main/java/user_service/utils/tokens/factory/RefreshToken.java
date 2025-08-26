package user_service.utils.tokens.factory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import user_service.config.JwtConfig;
import user_service.utils.tokens.interfaces.JwtToken;

@Component
@RequiredArgsConstructor
@Setter
public class RefreshToken implements JwtToken {
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private String subject;
    private Map<String, Object> claims;

    @Override
    public String generate() {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtConfig.getAccessTokenExpiration())))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean validate(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getType() {
        return "Refresh";
    }

    @Override
    public Claims getClaims() {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(generate())
                .getBody();
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public LocalDateTime expiresAt() {
        return LocalDateTime.now().plusSeconds(jwtConfig.getRefreshTokenExpiration() / 1000);
    }

}
