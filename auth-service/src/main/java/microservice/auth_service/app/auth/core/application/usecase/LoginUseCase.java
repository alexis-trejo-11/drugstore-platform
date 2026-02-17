package microservice.auth_service.app.auth.core.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.auth_service.app.User;
import microservice.auth_service.app.auth.core.application.command.login.LoginCommand;
import microservice.auth_service.app.auth.core.application.events.UserLoginEvent;
import microservice.auth_service.app.auth.core.application.result.SessionResult;
import microservice.auth_service.app.auth.core.domain.exceptions.InvalidCredentialsException;
import microservice.auth_service.app.auth.core.domain.valueobjects.Token;
import microservice.auth_service.app.auth.core.ports.input.UserEventPublisher;
import microservice.auth_service.app.auth.core.ports.output.PasswordEncoder;
import microservice.auth_service.app.auth.core.ports.output.TokenProvider;
import microservice.auth_service.app.auth.core.ports.output.UserServiceClient;

/**
 * LoginUseCase - Handles user authentication and session creation
 * This DDD ApplicationService validates credentials, creates JWT tokens, and
 * publishes login events
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LoginUseCase {
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;
  private final UserEventPublisher eventPublisher;
  private final UserServiceClient userServiceClient;

  /**
   * Execute the login use case
   *
   * @param command the login command containing email/phone and password
   * @return SessionResult containing JWT tokens for the authenticated user
   * @throws InvalidCredentialsException if credentials are invalid
   */
  public SessionResult execute(LoginCommand command) {
    log.info("Processing login attempt for identifier: {}", maskSensitiveData(command.identifier()));

    User user = getUserByIdentifier(command.identifier());
    log.debug("User found with ID: {}", user.getId());

    validatePassword(command.password(), user);
    log.debug("Password validation successful for user: {}", user.getId());

    SessionResult sessionResult = generateSessionTokens(user);
    log.info("Session tokens generated successfully for user: {}", user.getId());

    publishLoginEvent(user);
    log.debug("UserLoginEvent published for user: {}", user.getId());

    return sessionResult;
  }

  /**
   * Validates the provided password against the user's stored password
   *
   * @param providedPassword the password provided by user
   * @param user             the user from repository
   * @throws InvalidCredentialsException if password is invalid
   */
  private void validatePassword(String providedPassword, User user) {
    log.debug("Validating password for user: {}", user.getId());

    if (!passwordEncoder.matches(providedPassword, user.getPassword())) {
      log.warn("Invalid password attempt for user: {}", user.getId());
      throw new InvalidCredentialsException("Invalid credentials: incorrect password");
    }
  }

  /**
   * Generates access and refresh JWT tokens for the authenticated user
   *
   * @param user the authenticated user
   * @return SessionResult containing both tokens
   */
  private SessionResult generateSessionTokens(User user) {
    log.debug("Generating session tokens for user: {}", user.getId());

    Token accessToken = tokenProvider.generateAccessToken(
        user.getId(),
        user.getEmail().value(),
        user.getRole());

    Token refreshToken = tokenProvider.generateRefreshToken(
        user.getId(),
        user.getEmail().value());

    var sessionResult = SessionResult.bearer(
        user.getId(),
        accessToken,
        refreshToken);

    log.debug("Session tokens generated for user: {}", user.getId());

    return sessionResult;
  }

  /**
   * Publishes a UserLoginEvent for audit trail and metrics
   *
   * @param user the logged-in user
   */
  private void publishLoginEvent(User user) {
    try {
      UserLoginEvent event = new UserLoginEvent(
          user.getId().value().toString(),
          user.getEmail().value(),
          UUID.randomUUID().toString());
      eventPublisher.publishUserLogin(event);
      log.debug("UserLoginEvent successfully published");
    } catch (Exception e) {
      log.warn("Failed to publish UserLoginEvent for user: {}", user.getId(), e);
      // Non-blocking: login should succeed even if event publishing fails
    }
  }

  /**
   * Masks sensitive data in logs (email/phone)
   *
   * @param identifier the email or phone to mask
   * @return masked identifier
   */
  private String maskSensitiveData(String identifier) {
    if (identifier == null || identifier.length() < 4) {
      return "***";
    }
    return identifier.substring(0, 3) + "***";
  }

  /**
   * Retrieves a user by email or phone number using the UserServiceClient
   *
   * @param identifier email or phone number
   * @return User object if found
   * @throws InvalidCredentialsException if user is not found
   */
  private User getUserByIdentifier(String identifier) {
    User user;
    if (identifier.contains("@")) {
      user = userServiceClient.getUserByEmail(identifier);

    } else {
      user = userServiceClient.getUserByPhone(identifier);
    }

    if (user == null) {
      log.warn("No user found with identifier: {}", maskSensitiveData(identifier));
      throw new InvalidCredentialsException("Invalid credentials: user not found");
    }
    return user;
  }
}
