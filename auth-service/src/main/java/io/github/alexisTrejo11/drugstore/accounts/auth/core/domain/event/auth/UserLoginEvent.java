package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth;

import java.time.LocalDateTime;

/**
 * Domain event representing user login.
 * Immutable record for publishing to event bus/Kafka.
 */
public record UserLoginEvent(
    String userId,
    String email,
    String sessionId,
    LocalDateTime timestamp) {
  /**
   * Creates a UserLoginEvent with automatic timestamp.
   */
  public UserLoginEvent(String userId, String email, String sessionId) {
    this(userId, email, sessionId, LocalDateTime.now());
  }
}
