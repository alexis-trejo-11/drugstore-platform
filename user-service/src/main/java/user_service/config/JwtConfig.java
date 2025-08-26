package user_service.config;

import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.security.Keys;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret;
    private long accessTokenExpirationMinutes = 60;
    private long refreshTokenExpirationMinutes = 43800; // 30 days

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
