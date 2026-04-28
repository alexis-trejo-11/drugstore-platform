package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.twofa;

import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.EnableTwoFactorCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.TwoFactorQRResult;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth.TwoFactorEnabledEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TokenService;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserEventPublisher;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;
import libs_kernel.security.dto.UserClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EnableTwoFactorAuthUseCase - Enables Two-Factor Authentication for a user.
 * This DDD ApplicationService generates setup code and publishes event.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnableTwoFactorAuthUseCase {
  private final UserServiceClient userServiceClient;
  private final TokenService tokenService;
  private final UserEventPublisher eventPublisher;

  /**
   * Execute the enable 2FA use case.
   *
   * @param command the enable 2FA command containing user ID
   * @return TwoFactorQRResult with QR code and setup information
   */
  public TwoFactorQRResult execute(EnableTwoFactorCommand command) {
    log.info("Processing enable 2FA request for user: {}", command.userId());

    User user = getUserById(command.userId() != null ? command.userId().value() : null);
    Token setupToken = generateSetupToken(user);
    String secretKey = generateSecretKey();
    String qrCodeUrl = generateQRCodeUrl(user, secretKey);

    // TODO: Store secret key temporarily until verified
    // userServiceClient.storeTwoFactorSecret(user.getId(), secretKey);

    publishTwoFactorEnabledEvent(user);

    log.info("2FA enabled successfully for user: {}", user.getId());

    return new TwoFactorQRResult(
        user.getId().value(),
        qrCodeUrl,
        secretKey,
        setupToken.code());
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
   * Generates 2FA setup token.
   */
  private Token generateSetupToken(User user) {
    log.debug("Generating 2FA setup token");

    UserClaims claims = UserClaims.builder()
        .userId(user.getId().value())
        .email(user.getEmail().value())
        .build();

    Token token = tokenService.generateToken(TokenType.TWO_FA, claims);

    log.debug("2FA setup token generated");
    return token;
  }

  /**
   * Generates secret key for TOTP.
   */
  private String generateSecretKey() {
    // TODO: Implement proper TOTP secret generation
    log.debug("Generating TOTP secret key");
    return "SECRET_KEY_PLACEHOLDER";
  }

  /**
   * Generates QR code URL for authenticator apps.
   */
  private String generateQRCodeUrl(User user, String secretKey) {
    // TODO: Implement proper TOTP QR code URL generation
    log.debug("Generating QR code URL");
    String appName = "DrugstorePlatform";
    return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
        appName,
        user.getEmail().value(),
        secretKey,
        appName);
  }

  /**
   * Publishes 2FA enabled event.
   */
  private void publishTwoFactorEnabledEvent(User user) {
    try {
      log.debug("Publishing 2FA enabled event");

      TwoFactorEnabledEvent event = new TwoFactorEnabledEvent(
          user.getId().value(),
          user.getEmail().value(),
          "TOTP");

      eventPublisher.publishTwoFactorEnabled(event);

      log.debug("2FA enabled event published successfully");
    } catch (Exception e) {
      log.error("Failed to publish 2FA enabled event: {}", e.getMessage(), e);
      // Non-blocking
    }
  }
}
