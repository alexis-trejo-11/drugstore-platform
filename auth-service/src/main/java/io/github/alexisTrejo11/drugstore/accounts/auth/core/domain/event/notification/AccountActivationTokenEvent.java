package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.notification;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

/**
 * Event published when an account activation token is generated.
 * This event triggers notification service to send activation link/code to
 * user.
 */
@Builder
public record AccountActivationTokenEvent(
    String eventId,
    String eventType,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime eventTimestamp,
    String correlationId,
    String userId,
    String email,
    String firstName,
    String activationToken,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime expiresAt,
    String language) {
  public static AccountActivationTokenEvent create(
      String userId,
      String email,
      String firstName,
      String activationToken,
      LocalDateTime expiresAt) {
    return AccountActivationTokenEvent.builder()
        .eventId(java.util.UUID.randomUUID().toString())
        .eventType("ACCOUNT_ACTIVATION_TOKEN_GENERATED")
        .eventTimestamp(LocalDateTime.now())
        .correlationId(java.util.UUID.randomUUID().toString())
        .userId(userId)
        .email(email)
        .firstName(firstName)
        .activationToken(activationToken)
        .expiresAt(expiresAt)
        .language("en")
        .build();
  }
}
