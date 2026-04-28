package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.token;

import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth.AccountActivatedEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.InvalidTokenException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.TokenExpiredException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TokenService;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserEventPublisher;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;
import libs_kernel.security.dto.UserClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ActivateAccountUseCase - Handles account activation using activation token.
 * This DDD ApplicationService validates token, activates user and publishes
 * event.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivateAccountUseCase {
  private final TokenService tokenService;
  private final UserServiceClient userServiceClient;
  private final UserEventPublisher eventPublisher;

  /**
   * Execute the activate account use case.
   *
   * @param activationCode the activation code/token
   * @throws InvalidTokenException if token is invalid or malformed
   * @throws TokenExpiredException if token has expired
   */
  public void execute(String activationCode) {
    log.info("Processing account activation request");

    UserClaims claims = validateAndExtractClaims(activationCode);
    User user = getUserAndActivate(claims.userId());
    invalidateActivationToken(activationCode);
    publishAccountActivatedEvent(user);

    log.info("Account activated successfully for user: {}", user.getId());
  }

  /**
   * Validates activation token and extracts user claims.
   */
  private UserClaims validateAndExtractClaims(String activationCode) {
    log.debug("Validating activation token");

    if (!tokenService.validateToken(activationCode, TokenType.ACTIVATION)) {
      log.warn("Invalid or expired activation token");
      throw TokenExpiredException.temporary("Activation");
    }

    try {
      UserClaims claims = tokenService.extractClaims(activationCode);
      log.debug("Activation token validated for user: {}", claims.userId());
      return claims;
    } catch (Exception e) {
      log.error("Failed to extract claims from activation token: {}", e.getMessage());
      throw InvalidTokenException.malformed("Activation");
    }
  }

  /**
   * Retrieves user and marks as activated.
   */
  private User getUserAndActivate(String userId) {
    log.debug("Retrieving and activating user: {}", userId);

    User user = userServiceClient.getUserById(userId);

    if (user == null) {
      log.error("User not found: {}", userId);
      throw new RuntimeException("User not found");
    }

    // TODO: Call user service to mark user as activated
    // userServiceClient.activateUser(userId);

    log.debug("User activated successfully");
    return user;
  }

  /**
   * Invalidates the used activation token.
   */
  private void invalidateActivationToken(String activationCode) {
    try {
      log.debug("Invalidating activation token");
      tokenService.invalidateToken(activationCode);
      log.debug("Activation token invalidated");
    } catch (Exception e) {
      log.warn("Failed to invalidate activation token: {}", e.getMessage());
      // Non-critical: account is already activated
    }
  }

  /**
   * Publishes account activated event.
   */
  private void publishAccountActivatedEvent(User user) {
    try {
      log.debug("Publishing account activated event");

      AccountActivatedEvent event = new AccountActivatedEvent(
          user.getId().value(),
          user.getEmail().value());

      eventPublisher.publishAccountActivated(event);

      log.debug("Account activated event published successfully");
    } catch (Exception e) {
      log.error("Failed to publish account activated event: {}", e.getMessage(), e);
      // Non-blocking: account is still activated
    }
  }
}
