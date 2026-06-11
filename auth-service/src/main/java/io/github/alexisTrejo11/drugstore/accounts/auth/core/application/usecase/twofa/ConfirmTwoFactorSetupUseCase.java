package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.twofa;

import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.persitence.RedisPendingTotpSecretRepository;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.ConfirmTwoFactorSetupCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth.TwoFactorEnabledEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.TwoFactorAuthenticationException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserEventPublisher;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmTwoFactorSetupUseCase {

  private final RedisPendingTotpSecretRepository pendingSecrets;
  private final UserServiceClient userServiceClient;
  private final UserEventPublisher eventPublisher;
  private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

  public void execute(ConfirmTwoFactorSetupCommand command) {
    User user =
        userServiceClient.getUserById(command.userId().value());
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    if (user.isTwoFactorEnabled()) {
      throw TwoFactorAuthenticationException.alreadyEnabled();
    }

    String secret =
        pendingSecrets
            .get(command.userId().value())
            .orElseThrow(() -> TwoFactorAuthenticationException.invalidCode());

    int code;
    try {
      code = Integer.parseInt(command.verificationCode().replaceAll("\\s+", ""));
    } catch (NumberFormatException e) {
      throw TwoFactorAuthenticationException.invalidCode();
    }

    if (!googleAuthenticator.authorize(secret, code)) {
      throw TwoFactorAuthenticationException.invalidCode();
    }

    userServiceClient.completeTwoFactorEnrollment(user.getId().value(), secret);
    pendingSecrets.delete(command.userId().value());

    publishEnabled(user);
    log.info("2FA enrollment completed for user {}", user.getId().value());
  }

  private void publishEnabled(User user) {
    try {
      TwoFactorEnabledEvent event =
          new TwoFactorEnabledEvent(user.getId().value(), user.getEmail().value(), "TOTP");
      eventPublisher.publishTwoFactorEnabled(event);
    } catch (Exception e) {
      log.warn("Failed to publish TwoFactorEnabledEvent: {}", e.getMessage());
    }
  }
}
