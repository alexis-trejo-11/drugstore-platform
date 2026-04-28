package io.github.alexisTrejo11.drugstore.users.user.core.application.handlers.command;

import io.github.alexisTrejo11.drugstore.users.user.core.application.command.UpdateUserCommand;
import io.github.alexisTrejo11.drugstore.users.user.core.application.result.CommandResult;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.input.CommandHandler;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.exceptions.UserNotFoundError;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.output.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserIdentifiersCommandHandler implements CommandHandler<UpdateUserCommand> {
    private final UserRepository userRepository;

    @Autowired
    public UpdateUserIdentifiersCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CommandResult handle(UpdateUserCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundError(command.userId()));

        user.updateAuthFields(
                command.email(),
                command.phoneNumber());
        userRepository.save(user);

        return CommandResult.success("User updated successfully", user);
    }
}
