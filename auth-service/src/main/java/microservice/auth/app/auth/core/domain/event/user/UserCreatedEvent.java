package microservice.auth.app.auth.core.domain.event.user;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

@Builder
public record UserCreatedEvent(
    String eventId,
    String eventType,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime eventTimestamp,
    String correlationId,
    String userId,
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    String password,
    String role,
    Boolean twoFactorEnabled,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt,
    String source,
    String ipAddress,
    String userAgent) {
}
