package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.token;

import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.SendValidationCodeCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.notification.TwoFactorCodeEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TokenService;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.NotificationEventPublisher;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;
import libs_kernel.security.dto.UserClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * SendValidationCodeUseCase - Sends 2FA validation code to user.
 * This DDD ApplicationService generates code and publishes event for
 * notification.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendValidationCodeUseCase {
  private final UserServiceClient userServiceClient;
  private final TokenService tokenService;
  private final NotificationEventPublisher notificationPublisher;

  /**
   * Execute the send validation code use case.
   *
   * @param command the send validation code command containing user ID
   */
  public void execute(SendValidationCodeCommand command) {
    log.info("Processing send validation code request for user: {}", command.userIdString());

    User user = getUserById(command.userIdString());
    Token validationToken = generateValidationToken(user);
    publishValidationCodeEvent(user, validationToken);

    log.info("Validation code generated and sent for user: {}", user.getId());
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
   * Generates 2FA validation token.
   */
  private Token generateValidationToken(User user) {
    log.debug("Generating 2FA validation token");

    UserClaims claims = UserClaims.builder()
        .userId(user.getId().value())
        .email(user.getEmail().value())
        .build();

    Token token = tokenService.generateToken(TokenType.TWO_FA, claims);

    log.debug("2FA validation token generated");
    return token;
  }

  /**
   * Publishes validation code event for notification service.
   */
  private void publishValidationCodeEvent(User user, Token token) {
    try {
      log.debug("Publishing 2FA validation code event");

      TwoFactorCodeEvent event = TwoFactorCodeEvent.builder()
          .eventId(java.util.UUID.randomUUID().toString())
          .eventType("TWO_FACTOR_CODE_REQUESTED")
          .eventTimestamp(LocalDateTime.now())
          .correlationId(java.util.UUID.randomUUID().toString())
          .userId(user.getId().value())
          .email(user.getEmail().value())
          .phoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber().value() : null)
          .firstName(user.getFirstName())
          .code(token.code())
          .expiresAt(token.expiresAt())
          .channel(TwoFactorCodeEvent.NotificationChannel.EMAIL)
          .purpose("LOGIN_VERIFICATION")
          .language("en")
          .build();

      notificationPublisher.publishTwoFactorCode(event);

      log.debug("2FA validation code event published successfully");
    } catch (Exception e) {
      log.error("Failed to publish 2FA validation code event: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to send validation code", e);
    }
  }
}
