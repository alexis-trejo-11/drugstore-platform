package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth;

import java.time.LocalDateTime;

/**
 * Domain event representing account activation.
 * Published when user successfully activates their account.
 */
public record AccountActivatedEvent(
    String userId,
    String email,
    LocalDateTime timestamp) {
  public AccountActivatedEvent(String userId, String email) {
    this(userId, email, LocalDateTime.now());
  }
}
