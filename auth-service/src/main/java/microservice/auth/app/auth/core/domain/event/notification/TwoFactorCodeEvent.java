package microservice.auth.app.auth.core.domain.event.notification;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

@Builder
public record TwoFactorCodeEvent(
    String eventId,
    String eventType,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime eventTimestamp,
    String correlationId,
    String userId,
    String email,
    String phoneNumber,
    String firstName,
    String code,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime expiresAt,
    NotificationChannel channel,
    String purpose,
    String ipAddress,
    String deviceName,
    String language) {
  public enum NotificationChannel {
    EMAIL,
    SMS,
    BOTH
  }
}
