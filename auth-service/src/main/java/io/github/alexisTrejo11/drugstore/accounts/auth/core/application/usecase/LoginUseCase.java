package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase;

import java.util.UUID;

import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SessionPayload;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models.JWTSessions;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TokenService;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.*;
import libs_kernel.security.dto.UserClaims;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.accounts.auth.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.LoginCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth.UserLoginEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.InvalidCredentialsException;

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
  private final TokenService tokenProvider;
  private final UserEventPublisher eventPublisher;
  private final SessionRepository sessionRepository;
  private final UserServiceClient userServiceClient;

  /**
   * Execute the login use case
   *
   * @param command the login command containing email/phone and password
   * @return SessionPayload containing JWT tokens for the authenticated user
   * @throws InvalidCredentialsException if credentials are invalid
   */
  public SessionPayload execute(LoginCommand command) {
    log.info("Processing login attempt for identifier: {}", maskSensitiveData(command.identifier()));
    User user = getUserByIdentifier(command.identifier());

    validatePassword(command.password(), user);
    user.validateUserCanLogin();

    SessionPayload sessionPayload = processJwtSession(user, command);
    publishLoginEvent(user);

    return sessionPayload;
  }

  /**
   * Generates access and refresh JWT tokens for the authenticated user
   *
   * @param user the authenticated user
   * @return SessionPayload containing both tokens
   */
  private SessionPayload processJwtSession(User user, LoginCommand command) {
    log.debug("Generating session tokens for user: {}", user.getId());

    var claims = UserClaims.builder()
        .userId(user.getId().value())
        .email(user.getEmail().value())
        .role(user.getRole().getRoleName())
        .name(user.getFirstName() + " " + user.getLastName())
        .phoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber().value() : null)
        .build();

    // Interface Implementation will ignore unnecessary claims based on token type
    var accessToken = tokenProvider.generateToken(TokenType.ACCESS, claims);
    var refreshToken = tokenProvider.generateToken(TokenType.REFRESH, claims);

    // Save it on session service for track and blacklist if required
    var jwtSession = JWTSessions.from(
        refreshToken,
        command.deviceId(),
        command.ipAddress(),
        user.getId().value());
    sessionRepository.save(jwtSession);

    log.debug("Session tokens generated for user: {}", user.getId());
    return SessionPayload.bearer(user.getId().value(), accessToken, refreshToken);
  }

  private void validatePassword(String providedPassword, User user) {
    log.debug("Validating password for user: {}", user.getId());

    if (!passwordEncoder.matches(providedPassword, user.getPassword())) {
      log.warn("Invalid password attempt for user: {}", user.getId());
      throw new InvalidCredentialsException("Invalid credentials: incorrect password");
    }

    log.debug("Password validation successful for user: {}", user.getId());
  }

  // Event publishing is non-blocking - if it fails, we log the error but do not
  // prevent login
  // Will send to kafka to create user and send notification if required
  private void publishLoginEvent(User user) {
    try {
      UserLoginEvent event = new UserLoginEvent(
          user.getId().value(),
          user.getEmail().value(),
          UUID.randomUUID().toString());
      eventPublisher.publishUserLogin(event);
      log.debug("UserLoginEvent successfully published");
    } catch (Exception e) {
      log.warn("Failed to publish UserLoginEvent for user: {}", user.getId(), e);
      // Non-blocking: login should succeed even if event publishing fails
    }
  }

  private String maskSensitiveData(String identifier) {
    if (identifier == null || identifier.length() < 4) {
      return "***";
    }
    return identifier.substring(0, 3) + "***";
  }

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

    log.debug("User found with ID: {}", user.getId());
    return user;
  }
}
