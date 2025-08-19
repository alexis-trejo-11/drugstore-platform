package microservice.auth.app.core.models;

import lombok.Getter;
import lombok.Setter;
import microservice.auth.app.core.valueobjects.Email;
import microservice.auth.app.core.valueobjects.UserId;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
public class Session {
    private String sessionId;
    private UserId userId;
    private Email email;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public Session(String sessionId, UserId userId, Email email, Duration duration) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plus(duration);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}