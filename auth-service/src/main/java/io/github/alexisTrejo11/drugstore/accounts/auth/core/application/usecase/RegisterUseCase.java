package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase;

import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.SignupCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SignUpResult;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth.UserRegisteredEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.notification.AccountActivationTokenEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.UserAlreadyExistsError;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TokenService;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.NotificationEventPublisher;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.PasswordEncoder;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserEventPublisher;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models.User;
import libs_kernel.security.dto.UserClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;
  private final NotificationEventPublisher notificationEventPublisher;

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

    if (command.personalInfo() == null) {
      throw new IllegalArgumentException("Personal information is required to register");
    }

    String hashedPassword = passwordEncoder.encode(command.password().value());

    User created =
        userServiceClient.createUser(
            command.email().value(),
            command.phone().value(),
            command.personalInfo().firstName(),
            command.personalInfo().lastName(),
            hashedPassword,
            command.role().name());

    log.debug("User persisted in user-service with id: {}", created.getId());

    publishUserRegisteredEvent(created);
    publishActivationTokenNotification(created);

    return SignUpResult.success(created, true);
  }

  private void validateUserUniqueness(SignupCommand command) {
    log.debug(
        "Validating uniqueness for email: {} and phone: {}",
        command.email().value(),
        command.phone().value());

    if (!userServiceClient.isEmailUnique(command.email().value())) {
      log.warn("Registration attempt with existing email: {}", command.email().value());
      throw new UserAlreadyExistsError("Email already exists: " + command.email().value());
    }

    if (!userServiceClient.isPhoneUnique(command.phone().value())) {
      log.warn("Registration attempt with existing phone: {}", command.phone().value());
      throw new UserAlreadyExistsError("Phone number already exists: " + command.phone().value());
    }
  }

  private void publishUserRegisteredEvent(User user) {
    try {
      UserRegisteredEvent event =
          new UserRegisteredEvent(user.getId(), user.getEmail(), user.getPhoneNumber());
      eventPublisher.publishUserRegistered(event);
      log.debug("UserRegisteredEvent successfully published for user: {}", user.getId());
    } catch (Exception e) {
      log.error("Failed to publish UserRegisteredEvent for user: {}", user.getId(), e);
      throw new RuntimeException("Event publishing failed", e);
    }
  }

  private void publishActivationTokenNotification(User user) {
    try {
      UserClaims claims =
          UserClaims.builder()
              .userId(user.getId().value())
              .email(user.getEmail().value())
              .name(user.getFirstName() + " " + user.getLastName())
              .role(user.getRole().getRoleName())
              .build();

      Token activationToken = tokenService.generateToken(TokenType.ACTIVATION, claims);

      AccountActivationTokenEvent event =
          AccountActivationTokenEvent.create(
              user.getId().value(),
              user.getEmail().value(),
              user.getFirstName(),
              activationToken.code(),
              activationToken.expiresAt());

      notificationEventPublisher.publishAccountActivationToken(event);
      log.debug("Account activation notification queued for user: {}", user.getId());
    } catch (Exception e) {
      log.error("Failed to publish account activation token event for user: {}", user.getId(), e);
      // Registration already succeeded in user-service; notification can be retried elsewhere
    }
  }
}
