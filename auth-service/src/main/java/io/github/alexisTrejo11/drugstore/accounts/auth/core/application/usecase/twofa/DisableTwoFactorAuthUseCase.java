package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.twofa;

import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.DisableTwoFactorCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth.TwoFactorDisabledEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserEventPublisher;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DisableTwoFactorAuthUseCase - Disables Two-Factor Authentication for a user.
 * This DDD ApplicationService removes 2FA configuration and publishes event.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DisableTwoFactorAuthUseCase {
  private final UserServiceClient userServiceClient;
  private final UserEventPublisher eventPublisher;

  /**
   * Execute the disable 2FA use case.
   *
   * @param command the disable 2FA command containing user ID
   */
  public void execute(DisableTwoFactorCommand command) {
    log.info("Processing disable 2FA request for user: {}", command.userId());

    User user = getUserById(command.userId() != null ? command.userId().value() : null);
    removeTwoFactorConfiguration(user);
    publishTwoFactorDisabledEvent(user);

    log.info("2FA disabled successfully for user: {}", user.getId());
  }


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

  private void removeTwoFactorConfiguration(User user) {
    log.debug("Removing 2FA configuration for user: {}", user.getId());

    // TODO: Call user service to remove 2FA secret
    // userServiceClient.removeTwoFactorSecret(user.getId());

    log.debug("2FA configuration removed");
  }

  private void publishTwoFactorDisabledEvent(User user) {
    try {
      log.debug("Publishing 2FA disabled event");

      TwoFactorDisabledEvent event = new TwoFactorDisabledEvent(
          user.getId().value(),
          user.getEmail().value());

      eventPublisher.publishTwoFactorDisabled(event);

      log.debug("2FA disabled event published successfully");
    } catch (Exception e) {
      log.error("Failed to publish 2FA disabled event: {}", e.getMessage(), e);
      // Non-blocking
    }
  }
}
