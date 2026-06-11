package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.twofa;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.persitence.RedisPendingTotpSecretRepository;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.EnableTwoFactorCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.TwoFactorQRResult;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.TwoFactorAuthenticationException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Starts TOTP enrollment: generates secret, stores it temporarily in Redis, returns otpauth URL.
 * User must call confirm endpoint with a valid authenticator code to finish enrollment.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnableTwoFactorAuthUseCase {
  private final UserServiceClient userServiceClient;
  private final RedisPendingTotpSecretRepository pendingSecrets;
  private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

  @Value("${token.two-fa.pending-setup-minutes:15}")
  private int pendingSetupMinutes;

  public TwoFactorQRResult execute(EnableTwoFactorCommand command) {
    log.info("Starting 2FA enrollment for user: {}", command.userId());

    User user = getUserById(command.userId().value());
    if (user.isTwoFactorEnabled()) {
      throw TwoFactorAuthenticationException.alreadyEnabled();
    }

    var credentials = googleAuthenticator.createCredentials();
    String secret = credentials.getKey();

    pendingSecrets.put(user.getId().value(), secret, Duration.ofMinutes(pendingSetupMinutes));

    String appName = "DrugstorePlatform";
    String qrUrl =
        String.format(
            "otpauth://totp/%s:%s?secret=%s&issuer=%s",
            appName, user.getEmail().value(), secret, appName);

    return new TwoFactorQRResult(user.getId().value(), qrUrl, secret, secret);
  }

  private User getUserById(String userId) {
    User user = userServiceClient.getUserById(userId);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    return user;
  }
}
