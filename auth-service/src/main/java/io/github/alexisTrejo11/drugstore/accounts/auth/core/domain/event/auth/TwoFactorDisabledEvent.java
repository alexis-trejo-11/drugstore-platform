package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth;

import java.time.LocalDateTime;

/**
 * Domain event representing Two-Factor Authentication being disabled.
 * Published when user successfully disables 2FA on their account.
 */
public record TwoFactorDisabledEvent(
    String userId,
    String email,
    LocalDateTime timestamp) {
  public TwoFactorDisabledEvent(String userId, String email) {
    this(userId, email, LocalDateTime.now());
  }
}
