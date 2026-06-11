package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.twofa;

import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.DisableTwoFactorCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth.TwoFactorDisabledEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserEventPublisher;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisableTwoFactorAuthUseCase {
  private final UserServiceClient userServiceClient;
  private final UserEventPublisher eventPublisher;

  public void execute(DisableTwoFactorCommand command) {
    log.info("Processing disable 2FA request for user: {}", command.userId());

    User user = getUserById(command.userId().value());
    userServiceClient.disableTwoFactorOnUser(user.getId().value());
    publishTwoFactorDisabledEvent(user);

    log.info("2FA disabled successfully for user: {}", user.getId());
  }

  private User getUserById(String userId) {
    User user = userServiceClient.getUserById(userId);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    return user;
  }

  private void publishTwoFactorDisabledEvent(User user) {
    try {
      TwoFactorDisabledEvent event =
          new TwoFactorDisabledEvent(user.getId().value(), user.getEmail().value());
      eventPublisher.publishTwoFactorDisabled(event);
    } catch (Exception e) {
      log.error("Failed to publish 2FA disabled event: {}", e.getMessage(), e);
    }
  }
}
