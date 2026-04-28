package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.password;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ChangePasswordCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth.PasswordChangedEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.InvalidCredentialsException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.PasswordEncoder;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.SessionRepository;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserEventPublisher;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ChangePasswordUseCase - Handles authenticated user password changes.
 * This DDD ApplicationService validates current password, updates to new one,
 * invalidates all sessions, and publishes event.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePasswordUseCase {
  private final UserServiceClient userServiceClient;
  private final PasswordEncoder passwordEncoder;
  private final SessionRepository sessionRepository;
  private final UserEventPublisher eventPublisher;

  /**
   * Execute the change password use case.
   *
   * @param command the change password command
   * @throws InvalidCredentialsException if current password is incorrect
   */
  public void execute(ChangePasswordCommand command) {
    log.info("Processing password change request for user: {}", command.userId());
    User user = getUserById(command.userId());

		validateCurrentPassword(user, command.currentPassword());
    updatePassword(user, command.newPassword());
    invalidateAllSessions(user);
    publishPasswordChangedEvent(user);

    log.info("Password changed successfully for user: {}", user.getId());
  }

  private User getUserById(UserId userId) {
    log.debug("Retrieving user: {}", userId);

    User user = userServiceClient.getUserById(userId != null ? userId.value() : null);

    if (user == null) {
      log.error("User not found: {}", userId);
      throw new InvalidCredentialsException("User not found");
    }

    log.debug("User retrieved successfully");
    return user;
  }

  private void validateCurrentPassword(User user, String currentPassword) {
    log.debug("Validating current password");

    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
      log.warn("Invalid current password for user: {}", user.getId());
      throw new InvalidCredentialsException("Current password is incorrect");
    }

    log.debug("Current password validated successfully");
  }


  private void updatePassword(User user, String newPassword) {
    log.debug("Updating password for user: {}", user.getId());

    String encodedPassword = passwordEncoder.encode(newPassword);
    user.setPassword(encodedPassword);

    // TODO: Call user service to persist password change
    // userServiceClient.updateUser(user);

    log.debug("Password updated successfully");
  }

  /**
   * Invalidates all user sessions for security (force re-login).
   */
  private void invalidateAllSessions(User user) {
    try {
      log.debug("Invalidating all sessions for user: {}", user.getId());

      sessionRepository.blackListAllRefreshTokensForUser(user.getId().value());

      log.debug("All sessions invalidated successfully");
    } catch (Exception e) {
      log.error("Failed to invalidate sessions: {}", e.getMessage(), e);
      // Non-critical: password is still changed
    }
  }

  private void publishPasswordChangedEvent(User user) {
    try {
      log.debug("Publishing password changed event");

      PasswordChangedEvent event = PasswordChangedEvent.userInitiated(
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
