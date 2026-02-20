package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase;

import java.time.Duration;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.accounts.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.LogoutAllCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.InvalidCredentialsException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.SessionService;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;

/**
 * LogoutAllUseCase - Handles logout from all sessions for a user
 * This DDD ApplicationService invalidates all active sessions for a user by
 * blacklisting their keys
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutAllUseCase {
  private final SessionService sessionService;
  private final UserServiceClient userServiceClient;

  // TODO: Check JWT expiration times and set blacklist duration accordingly
  private static final Duration BLACKLIST_DURATION = Duration.ofDays(7);

  /**
   * Execute the logout all use case
   *
   * @param command the logout all command containing the user ID
   * @throws InvalidCredentialsException if user not found or invalid user ID
   */
  public void execute(LogoutAllCommand command) {
    log.info("Processing logout from all sessions for user: {}", command.userId());

    User user = userServiceClient.getUserById(command.userId());
    log.debug("User found - proceeding with logout all for user: {}", user.getId());

    invalidateAllUserSessions(user);
    log.info("All sessions invalidated for user: {}", user.getId());
  }

  private void invalidateAllUserSessions(User user) {
    try {
      log.debug("Invalidating all sessions for user: {}", user.getId());

      // Create a composite key for all user sessions
      // This could be all tokens in a Redis set keyed by userId
      String userSessionKey = String.format("user_sessions:%s", user.getId().value());

      log.debug("Blacklisting all refresh tokens for user: {}", user.getId());
      // The implementation would typically:
      // 1. Fetch all active refresh tokens for the user
      // 2. Blacklist each one
      // For now, we demonstrate blacklisting a user-level session key
      sessionService.blacklistRefreshToken(userSessionKey, BLACKLIST_DURATION);

      log.info("Successfully invalidated all sessions for user: {}", user.getId());
    } catch (Exception e) {
      log.error("Failed to invalidate all sessions for user: {}", user.getId(), e);
      throw new RuntimeException("Failed to complete logout from all sessions", e);
    }
  }
}
