package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.notification;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

/**
 * Event published when a password reset token is requested.
 * This event triggers notification service to send reset link/code to user.
 */
@Builder
public record PasswordResetTokenEvent(
    String eventId,
    String eventType,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime eventTimestamp,
    String correlationId,
    String userId,
    String email,
    String firstName,
    String resetToken,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime expiresAt,
    String ipAddress,
    String language) {
  public static PasswordResetTokenEvent create(
      String userId,
      String email,
      String firstName,
      String resetToken,
      LocalDateTime expiresAt,
      String ipAddress) {
    return PasswordResetTokenEvent.builder()
        .eventId(java.util.UUID.randomUUID().toString())
        .eventType("PASSWORD_RESET_TOKEN_REQUESTED")
        .eventTimestamp(LocalDateTime.now())
        .correlationId(java.util.UUID.randomUUID().toString())
        .userId(userId)
        .email(email)
        .firstName(firstName)
        .resetToken(resetToken)
        .expiresAt(expiresAt)
        .ipAddress(ipAddress)
        .language("en")
        .build();
  }
}
