package io.github.alexisTrejo11.drugstore.users.user.adapter.output.messaging.kafka.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.users.user.core.application.command.DeleteUserCommand;
import io.github.alexisTrejo11.drugstore.users.user.core.application.result.CommandResult;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.events.UserDeletedEvent;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.input.CommandBus;

/**
 * Handler for UserDeletedEvent
 * <p>
 * Processes user deletion events from Kafka and delegates to the command bus.
 */
@Component
public class UserDeletedEventHandler {
  private final CommandBus commandBus;
  private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserDeletedEventHandler.class);

  @Autowired
  public UserDeletedEventHandler(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  public void handle(UserDeletedEvent event) {
    log.info("Processing UserDeletedEvent for userId: {}, email: {}", event.getUserId(), event.getEmail());

    try {
      DeleteUserCommand command = DeleteUserCommand.of(event.getUserId());

      CommandResult result = commandBus.dispatch(command);
      if (result.success()) {
        log.info("Successfully deleted user. UserId: {}, Email: {}",
            event.getUserId(), event.getEmail());
      } else {
        log.error("Failed to delete user for userId: {}. Message: {}",
            event.getUserId(), result.message());
        throw new RuntimeException("Failed to delete user: " + result.message());
      }
    } catch (Exception e) {
      log.error("Error handling UserDeletedEvent for user with ID: {}", event.getUserId(), e);
      throw new RuntimeException("Failed to process UserDeletedEvent", e);
    }
  }
}
