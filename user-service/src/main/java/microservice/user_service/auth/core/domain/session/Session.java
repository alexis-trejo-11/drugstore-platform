package microservice.user_service.auth.core.domain.session;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class Session {
    private UUID id;
    private UUID userId;
    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private String userAgent;
    private String ipAddress;
    private String device;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean active;

    public Session(UUID userId, String token) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.refreshToken = token;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(24);
        this.active = true;
    }



    public void deactivate() {
        this.active = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
