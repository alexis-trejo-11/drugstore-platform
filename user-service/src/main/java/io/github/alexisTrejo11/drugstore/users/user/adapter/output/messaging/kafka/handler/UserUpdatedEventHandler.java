package io.github.alexisTrejo11.drugstore.users.user.adapter.output.messaging.kafka.handler;

import io.github.alexisTrejo11.drugstore.users.user.core.application.command.UpdateUserCommand;
import io.github.alexisTrejo11.drugstore.users.user.core.application.result.CommandResult;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.events.UserUpdateEvent;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.input.CommandBus;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.output.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handler for UserUpdateEvent
 * <p>
 * Processes user update events including profile updates, last login updates,
 * and status changes.
 */
@Component
public class UserUpdatedEventHandler {
  private final CommandBus commandBus;
  private final UserRepository userRepository;
  private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserUpdatedEventHandler.class);

  @Autowired
  public UserUpdatedEventHandler(CommandBus commandBus, UserRepository userRepository) {
    this.commandBus = commandBus;
    this.userRepository = userRepository;
  }

  public void handle(UserUpdateEvent event) {
    log.info("Processing UserUpdateEvent for userId: {}, updateType: {}", event.getUserId(), event.getUpdateType());

    try {
      // Handle specific update types
      if ("LAST_LOGIN_UPDATE".equals(event.getUpdateType())) {
        handleLastLoginUpdate(event);
      } else {
        // Handle general profile update
        handleProfileUpdate(event);
      }

      log.info("Successfully processed UserUpdateEvent for userId: {}", event.getUserId());
    } catch (Exception e) {
      log.error("Error handling UserUpdateEvent for user with ID: {}", event.getUserId(), e);
      throw new RuntimeException("Failed to process UserUpdateEvent", e);
    }
  }

  /**
   * Handles last login update specifically
   */
  private void handleLastLoginUpdate(UserUpdateEvent event) {
    log.debug("Updating last login for userId: {}", event.getUserId());

    UserId userId = new UserId(event.getUserId());
    userRepository.updateLastLoginAsync(userId);

    log.info("Last login updated for userId: {}", event.getUserId());
  }

  /**
   * Handles general profile update (email, phone, etc.)
   */
  private void handleProfileUpdate(UserUpdateEvent event) {
    log.debug("Updating profile for userId: {}", event.getUserId());

    UpdateUserCommand command = new UpdateUserCommand(
        new UserId(event.getUserId()),
        event.getEmail() != null ? new Email(event.getEmail()) : null,
        event.getPhoneNumber() != null ? new PhoneNumber(event.getPhoneNumber()) : null);

    CommandResult result = commandBus.dispatch(command);
    if (result.success()) {
      log.info("Successfully updated user profile for userId: {}", event.getUserId());
    } else {
      log.error("Failed to update user profile for userId: {}. Message: {}", event.getUserId(), result.message());
      throw new RuntimeException("Failed to update user: " + result.message());
    }
  }
}
