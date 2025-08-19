package microservice.auth.app.application.events;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserLoginEvent {
    private String userId;
    private String email;
    private String sessionId;
    private LocalDateTime timestamp;

    public UserLoginEvent(String userId, String email, String sessionId) {
        this.userId = userId;
        this.email = email;
        this.sessionId = sessionId;
        this.timestamp = LocalDateTime.now();
    }
}
