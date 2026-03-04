package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.password;

import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ResetPasswordCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth.PasswordChangedEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.InvalidTokenException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.TokenExpiredException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TokenService;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.PasswordEncoder;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserEventPublisher;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;
import libs_kernel.security.dto.UserClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ResetPasswordUseCase - Handles password reset using a valid token.
 * This DDD ApplicationService validates token, updates password and publishes
 * event.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResetPasswordUseCase {
  private final TokenService tokenService;
  private final PasswordEncoder passwordEncoder;
  private final UserServiceClient userServiceClient;
  private final UserEventPublisher eventPublisher;

  /**
   * Execute the reset password use case.
   *
   * @param command the reset password command containing token and new password
   * @throws InvalidTokenException if token is invalid or malformed
   * @throws TokenExpiredException if token has expired
   */
  public void execute(ResetPasswordCommand command) {
    log.info("Processing password reset request");

    UserClaims claims = validateAndExtractClaims(command.token());
    User user = getUserById(claims.userId());
    updateUserPassword(user, command.newPassword());
    invalidateResetToken(command.token());
    publishPasswordChangedEvent(user);

    log.info("Password reset completed successfully for user: {}", user.getId());
  }

  /**
   * Validates reset token and extracts user claims.
   */
  private UserClaims validateAndExtractClaims(String resetToken) {
    log.debug("Validating reset token");

    if (!tokenService.validateToken(resetToken, TokenType.ACTIVATION)) {
      log.warn("Invalid or expired password reset token");
      throw TokenExpiredException.temporary("Password reset");
    }

    try {
      UserClaims claims = tokenService.extractClaims(resetToken);
      log.debug("Reset token validated for user: {}", claims.userId());
      return claims;
    } catch (Exception e) {
      log.error("Failed to extract claims from reset token: {}", e.getMessage());
      throw InvalidTokenException.malformed("Reset token");
    }
  }

  /**
   * Retrieves user by ID.
   */
  private User getUserById(String userId) {
    log.debug("Retrieving user: {}", userId);

    User user = userServiceClient.getUserById(userId);

    if (user == null) {
      log.error("User not found: {}", userId);
      throw new RuntimeException("User not found");
    }

    log.debug("User retrieved successfully");
    return user;
  }

  /**
   * Updates user password.
   */
  private void updateUserPassword(User user, String newPassword) {
    log.debug("Updating password for user: {}", user.getId());

    String encodedPassword = passwordEncoder.encode(newPassword);
    user.setPassword(encodedPassword);

    // TODO: Call user service to persist password change
    // userServiceClient.updateUser(user);

    log.debug("Password updated successfully");
  }

  /**
   * Invalidates the used reset token.
   */
  private void invalidateResetToken(String resetToken) {
    try {
      log.debug("Invalidating reset token");
      tokenService.invalidateToken(resetToken);
      log.debug("Reset token invalidated");
    } catch (Exception e) {
      log.warn("Failed to invalidate reset token: {}", e.getMessage());
      // Non-critical: password is already changed
    }
  }

  /**
   * Publishes password changed event.
   */
  private void publishPasswordChangedEvent(User user) {
    try {
      log.debug("Publishing password changed event");

      PasswordChangedEvent event = PasswordChangedEvent.passwordReset(
          user.getId().value(),
          user.getEmail().value());

      eventPublisher.publishPasswordChanged(event);

      log.debug("Password changed event published successfully");
    } catch (Exception e) {
      log.error("Failed to publish password changed event: {}", e.getMessage(), e);
      // Non-blocking: password is still changed
    }
  }
}
