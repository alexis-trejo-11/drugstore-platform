package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth;

import java.time.LocalDateTime;

/**
 * Domain event representing a password change.
 * Published when user successfully changes their password.
 */
public record PasswordChangedEvent(
    String userId,
    String email,
    LocalDateTime timestamp,
    String changeReason) {
  public PasswordChangedEvent(String userId, String email, String changeReason) {
    this(userId, email, LocalDateTime.now(), changeReason);
  }

  public static PasswordChangedEvent userInitiated(String userId, String email) {
    return new PasswordChangedEvent(userId, email, "USER_INITIATED");
  }

  public static PasswordChangedEvent passwordReset(String userId, String email) {
    return new PasswordChangedEvent(userId, email, "PASSWORD_RESET");
  }
}
