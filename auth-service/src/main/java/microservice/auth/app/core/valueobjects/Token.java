package microservice.auth.app.core.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;


public record Token (String token, String type, Duration expiresIn, LocalDateTime expiresAt) {
    public Token {
        if (token == null || type == null || expiresIn == null || expiresAt == null) {
            throw new IllegalArgumentException("Token, type, expiresIn, and expiresAt cannot be null");
        }
    }
}
