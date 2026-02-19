package io.github.alexisTrejo11.drugstore.users.app.user.core.application.handlers.command;

import io.github.alexisTrejo11.drugstore.users.app.user.core.application.command.UpdateUserStatusCommand;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.result.CommandResult;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input.CommandHandler;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.exceptions.UserNotFoundError;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.output.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserStatusCommandHandler implements CommandHandler<UpdateUserStatusCommand> {
  private final UserRepository userRepository;

  @Autowired
  public UpdateUserStatusCommandHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public CommandResult handle(UpdateUserStatusCommand command) {
    User user = userRepository.findById(command.userId())
        .orElseThrow(() -> new UserNotFoundError(command.userId()));

    switch (command.actions()) {
      case ACTIVATE -> user.activate();
      case BAN -> user.ban();
      case UNBAN -> user.unban();
      case DEACTIVATE -> user.deactivate();
      default -> throw new IllegalArgumentException("Invalid action: " + command.actions());
    }

    userRepository.save(user);

    return CommandResult.success("User activated successfully.");
  }
}
