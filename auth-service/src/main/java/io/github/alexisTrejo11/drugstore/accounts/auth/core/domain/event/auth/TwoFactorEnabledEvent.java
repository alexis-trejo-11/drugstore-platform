package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth;

import java.time.LocalDateTime;

/**
 * Domain event representing Two-Factor Authentication being enabled.
 * Published when user successfully enables 2FA on their account.
 */
public record TwoFactorEnabledEvent(
    String userId,
    String email,
    LocalDateTime timestamp,
    String method) {
  public TwoFactorEnabledEvent(String userId, String email, String method) {
    this(userId, email, LocalDateTime.now(), method);
  }
}
