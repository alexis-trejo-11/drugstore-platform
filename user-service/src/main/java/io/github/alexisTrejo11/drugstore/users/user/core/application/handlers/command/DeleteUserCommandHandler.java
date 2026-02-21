package io.github.alexisTrejo11.drugstore.users.user.core.application.handlers.command;

import io.github.alexisTrejo11.drugstore.users.user.core.application.command.DeleteUserCommand;
import io.github.alexisTrejo11.drugstore.users.user.core.application.result.CommandResult;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.input.CommandHandler;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.exceptions.UserNotFoundError;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.output.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserCommandHandler implements CommandHandler<DeleteUserCommand> {
    private final UserRepository userRepository;

    @Autowired
    public DeleteUserCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CommandResult handle(DeleteUserCommand command) {
        boolean exists = userRepository.existsById(command.userId());
        if (!exists) {
            throw new UserNotFoundError(command.userId());
        }

        userRepository.deleteById(command.userId());
        return CommandResult.success("User deleted successfully");
    }
}
