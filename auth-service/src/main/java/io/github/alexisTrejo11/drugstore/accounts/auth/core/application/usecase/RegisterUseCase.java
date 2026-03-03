package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.accounts.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.SignupCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth.UserRegisteredEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SignUpResult;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.UserAlreadyExistsError;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserEventPublisher;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;

/**
 * RegisterUseCase - Handles user registration/signup
 * This is a DDD ApplicationService that orchestrates the registration process
 * by validating unique credentials, creating the User aggregate, and publishing
 * domain events.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterUseCase {
  private final UserEventPublisher eventPublisher;
  private final UserServiceClient userServiceClient;

  /**
   * Execute the registration use case
   *
   * @param command the signup command containing user registration details
   * @return SignUpResult containing the newly created user ID and confirmation
   *         message
   * @throws UserAlreadyExistsError if email or phone already exists
   */
  public SignUpResult execute(SignupCommand command) {
    log.info("Processing user registration for email: {}", command.email().value());

    validateUserUniqueness(command);
    log.debug("Unique credentials validation passed for user: {}", command.email().value());

    User newUser = createUserFromCommand(command);
    log.debug("User aggregate created with role: {}", command.role().getRoleName());

    publishUserRegisteredEvent(newUser);
    log.debug("UserRegisteredEvent published for user ID: {}", newUser.getId());

    return SignUpResult.success(newUser, true); // Assuming email verification is required
  }

  /**
   * Validates that the email and phone number are unique in the system
   *
   * @param command the signup command
   * @throws UserAlreadyExistsError if email or phone already exists
   */
  private void validateUserUniqueness(SignupCommand command) {
    log.debug("Validating uniqueness for email: {} and phone: {}",
        command.email().value(), command.phone().value());

    // Check email uniqueness
    if (!userServiceClient.isEmailUnique(command.email().value())) {
      log.warn("Registration attempt with existing email: {}", command.email().value());
      throw new UserAlreadyExistsError("Email already exists: " + command.email().value());
    }

    // Check phone uniqueness
    if (!userServiceClient.isPhoneUnique(command.phone().value())) {
      log.warn("Registration attempt with existing phone: {}", command.phone().value());
      throw new UserAlreadyExistsError("Phone number already exists: " + command.phone().value());
    }
  }

  /**
   * Creates a User aggregate from the signup command
   * This follows the aggregate creation pattern in DDD
   *
   * @param command the signup command
   * @return the newly created User aggregate
   */
  private User createUserFromCommand(SignupCommand command) {
    return User.builder()
        .email(command.email())
        .phoneNumber(command.phone())
        .password(command.password() != null ? command.password().value() : "")
        .role(command.role())
        .build();
  }

  /**
   * Publishes the UserRegisteredEvent to notify other bounded contexts
   *
   * @param user the registered user aggregate
   */
  private void publishUserRegisteredEvent(User user) {
    try {
      UserRegisteredEvent event = new UserRegisteredEvent(
          user.getId(),
          user.getEmail(),
          user.getPhoneNumber());
      eventPublisher.publishUserRegistered(event);
      log.debug("UserRegisteredEvent successfully published for user: {}", user.getId());
    } catch (Exception e) {
      log.error("Failed to publish UserRegisteredEvent for user: {}", user.getId(), e);
      // In a production system, consider using an outbox pattern to ensure event
      // delivery
      throw new RuntimeException("Event publishing failed", e);
    }
  }
}
