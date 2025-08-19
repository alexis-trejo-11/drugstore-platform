package microservice.auth.app.infrastructure.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.auth.app.core.models.Session;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AuthResponseDto {
    private String sessionId;
    private String userId;
    private String email;
    private LocalDateTime expiresAt;

    public AuthResponseDto(Session session) {
        this.sessionId = session.getSessionId();
        this.userId = session.getUserId().toString();
        this.email = session.getEmail().value();
        this.expiresAt = session.getExpiresAt();
    }

}