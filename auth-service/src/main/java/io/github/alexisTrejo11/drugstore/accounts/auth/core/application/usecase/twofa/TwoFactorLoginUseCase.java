package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.twofa;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SessionPayload;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models.JWTSessions;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.SessionRepository;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.TwoFactorLoginCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth.UserLoginEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.TwoFactorAuthenticationException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TokenService;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserEventPublisher;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;
import libs_kernel.security.dto.UserClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TwoFactorLoginUseCase - Completes login after 2FA verification.
 * This DDD ApplicationService verifies 2FA code and creates authenticated
 * session.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TwoFactorLoginUseCase {
  private final UserServiceClient userServiceClient;
  private final TokenService tokenService;
  private final SessionRepository sessionRepository;
  private final UserEventPublisher eventPublisher;

  /**
   * Execute the two-factor login use case.
   *
   * @param command the 2FA login command containing user ID and verification code
   * @return authentication token for the session
   * @throws TwoFactorAuthenticationException if 2FA code is invalid
   */
  public SessionPayload execute(TwoFactorLoginCommand command) {
    log.info("Processing 2FA login for user: {}", command.email());

    verifyTwoFactorToken(command.email(), command.code());
    User user = getUserByEmail(command.email());

    SessionPayload sessionPayload = createSession(user, command);
    publishLoginEvent(user, sessionPayload.refreshToken().code());

    tokenService.invalidateToken(command.code());

    log.info("2FA login successful for user: {}", user.getId());
    return sessionPayload;
  }

  private void verifyTwoFactorToken(String email, String code) {
    log.debug("Verifying 2FA code");

    if (code == null || code.isBlank()) {
      throw TwoFactorAuthenticationException.invalidCode();
    }

    if (!tokenService.validateToken(code, TokenType.TWO_FA)) {
      throw TwoFactorAuthenticationException.invalidCode();
    }

    Token stored = tokenService.getToken(code);
    if (stored == null || stored.claims() == null) {
      throw TwoFactorAuthenticationException.invalidCode();
    }

    if (!email.trim().equalsIgnoreCase(stored.claims().email())) {
      log.warn("2FA code email mismatch");
      throw TwoFactorAuthenticationException.invalidCode();
    }
  }

  private User getUserByEmail(String email) {
    log.debug("Retrieving user: {}", email);

    User user = userServiceClient.getUserByEmail(email);

    if (user == null) {
      log.error("User not found: {}", email);
      throw new RuntimeException("User not found");
    }

    log.debug("User retrieved successfully");
    return user;
  }

  private SessionPayload createSession(User user, TwoFactorLoginCommand command) {
    log.debug("Creating session for user");

    log.debug("Generating session tokens for user: {}", user.getId());

    var claims =
        UserClaims.builder()
            .userId(user.getId().value())
            .email(user.getEmail().value())
            .role(user.getRole().getRoleName())
            .name(user.getFirstName() + " " + user.getLastName())
            .phoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber().value() : null)
            .build();

    var accessToken = tokenService.generateToken(TokenType.ACCESS, claims);
    var refreshToken = tokenService.generateToken(TokenType.REFRESH, claims);

    var jwtSession =
        JWTSessions.from(
            refreshToken, command.deviceId(), command.ipAddress(), user.getId().value());
    sessionRepository.save(jwtSession);

    log.debug("Session tokens generated for user: {}", user.getId());
    return SessionPayload.bearer(user.getId().value(), accessToken, refreshToken);
  }

  private void publishLoginEvent(User user, String sessionId) {
    try {
      log.debug("Publishing user login event");

      UserLoginEvent event =
          new UserLoginEvent(user.getId().value(), user.getEmail().value(), sessionId);

      eventPublisher.publishUserLogin(event);

      log.debug("User login event published successfully");
    } catch (Exception e) {
      log.error("Failed to publish user login event: {}", e.getMessage(), e);
      // Non-blocking: session is already created
    }
  }
}
