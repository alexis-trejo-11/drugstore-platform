package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.accounts.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.LogoutAllCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.SessionRepository;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;

/**
 * LogoutAllUseCase - Handles logout from all sessions for a user.
 * This DDD ApplicationService invalidates all active sessions for a user by
 * blacklisting their refresh tokens permanently.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutAllUseCase {
  private final SessionRepository sessionRepository;
  private final UserServiceClient userServiceClient;

  public void execute(LogoutAllCommand command) {
    log.info("Processing logout from all sessions for user: {}", command.userId());

    validateUserExists(command.userId());
    invalidateAllUserSessions(command.userId());

    log.info("All sessions successfully invalidated for user: {}", command.userId());
  }

  /**
   * Validates that the user exists before proceeding with logout.
   */
  private void validateUserExists(String userId) {
    log.debug("Validating user exists: {}", userId);

    User user = userServiceClient.getUserById(userId);

    log.debug("User validation successful for userId: {}", user.getId());
  }

  private void invalidateAllUserSessions(String userId) {
    try {
      log.debug("Blacklisting all refresh tokens for user: {}", userId);

      sessionRepository.blackListAllRefreshTokensForUser(userId);

      log.debug("Successfully blacklisted all tokens for user: {}", userId);
    } catch (Exception e) {
      log.error("Failed to blacklist tokens for user: {}. Error: {}", userId, e.getMessage(), e);
      throw e;
    }
  }
}
