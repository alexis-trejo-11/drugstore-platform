package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

import java.time.LocalDateTime;

/**
 * Domain event representing user registration.
 * Immutable record for publishing to event bus/Kafka.
 */
public record UserRegisteredEvent(
    String userId,
    String email,
    String phone,
    LocalDateTime timestamp) {
  /**
   * Creates a UserRegisteredEvent from domain value objects.
   * Timestamp is automatically set to current time.
   */
  public UserRegisteredEvent(UserId id, Email email, PhoneNumber phoneNumber) {
    this(id.value(), email.value(), phoneNumber.value(), LocalDateTime.now());
  }
}
