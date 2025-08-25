package microservice.user_service.auth.core.domain.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session implements Serializable {

    private UUID id;
    private UUID userId;
    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private String type = "Bearer";

    @Builder.Default
    private String userAgent = "";

    @Builder.Default
    private String ipAddress = "";

    @Builder.Default
    private String device = "";

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @Builder.Default
    private boolean active = true;

    public Session(UUID userId, String token) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.refreshToken = token;
        this.accessToken = "";
        this.type = "Bearer";
        this.userAgent = "";
        this.ipAddress = "";
        this.device = "";
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(24);
        this.active = true;
    }

    @JsonIgnore
    public long getExpiresInMillis() {
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).toMillis();
    }

    @JsonIgnore
    public void deactivate() {
        this.active = false;
    }

    @JsonIgnore
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public static Session createSafeSession(UUID userId, String accessToken, String refreshToken,
            String userAgent, String ipAddress, String device) {
        return Session.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .accessToken(accessToken != null ? accessToken : "")
                .refreshToken(refreshToken != null ? refreshToken : "")
                .type("Bearer")
                .userAgent(userAgent != null ? userAgent : "")
                .ipAddress(ipAddress != null ? ipAddress : "")
                .device(device != null ? device : "")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .active(true)
                .build();
    }
}