package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.password;

import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ForgotPasswordCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.notification.PasswordResetTokenEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TokenService;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.NotificationEventPublisher;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;
import libs_kernel.security.dto.UserClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ForgotPasswordUseCase - Handles password reset token generation and
 * notification.
 * This DDD ApplicationService generates a reset token and publishes event for
 * notification.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ForgotPasswordUseCase {
  private final UserServiceClient userServiceClient;
  private final TokenService tokenService;
  private final NotificationEventPublisher notificationPublisher;

  /**
   * Execute the forgot password use case.
   * Generates reset token and publishes event for email notification.
   *
   * @param command the forgot password command containing user email
   */
  public void execute(ForgotPasswordCommand command) {
    log.info("Processing forgot password request for email: {}", maskEmail(command.email()));

    User user = getUserByEmail(command.email());
    Token resetToken = generateResetToken(user);
    publishResetTokenEvent(user, resetToken, command.ipAddress());

    log.info("Password reset token generated and notification sent for user: {}", user.getId());
  }

  /**
   * Retrieves user by email.
   */
  private User getUserByEmail(String email) {
    log.debug("Looking up user by email");

    User user = userServiceClient.getUserByEmail(email);

    if (user == null) {
      log.warn("User not found for forgot password request");
      // Security: Don't reveal if email exists or not
      // Still return successfully but don't send email
      return null;
    }

    log.debug("User found: {}", user.getId());
    return user;
  }

  /**
   * Generates password reset token.
   */
  private Token generateResetToken(User user) {
    if (user == null) {
      return null;
    }

    log.debug("Generating password reset token for user: {}", user.getId());

    UserClaims claims = UserClaims.builder()
        .userId(user.getId().value())
        .email(user.getEmail().value())
        .build();

    Token token = tokenService.generateToken(TokenType.ACTIVATION, claims);

    log.debug("Password reset token generated successfully");
    return token;
  }

  /**
   * Publishes password reset token event for notification service.
   */
  private void publishResetTokenEvent(User user, Token token, String ipAddress) {
    if (user == null || token == null) {
      log.debug("Skipping event publication - user or token is null");
      return;
    }

    try {
      log.debug("Publishing password reset token event");

      PasswordResetTokenEvent event = PasswordResetTokenEvent.create(
          user.getId().value(),
          user.getEmail().value(),
          user.getFirstName(),
          token.code(),
          token.expiresAt(),
          ipAddress);

      notificationPublisher.publishPasswordResetToken(event);

      log.debug("Password reset token event published successfully");
    } catch (Exception e) {
      log.error("Failed to publish password reset token event: {}", e.getMessage(), e);
      // Non-blocking: token is still generated even if notification fails
    }
  }

  /**
   * Masks email for security logging.
   */
  private String maskEmail(String email) {
    if (email == null || !email.contains("@")) {
      return "***";
    }
    int atIndex = email.indexOf("@");
    if (atIndex <= 2) {
      return "***" + email.substring(atIndex);
    }
    return email.substring(0, 2) + "***" + email.substring(atIndex);
  }
}
