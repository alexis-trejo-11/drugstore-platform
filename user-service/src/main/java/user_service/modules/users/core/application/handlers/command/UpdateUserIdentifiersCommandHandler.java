package user_service.modules.users.core.application.handlers.command;

import user_service.modules.users.core.application.command.UpdateUserCommand;
import user_service.modules.users.core.application.result.CommandResult;
import user_service.modules.users.core.ports.input.CommandHandler;
import user_service.modules.users.core.domain.exceptions.UserNotFoundError;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.output.UserRepository;
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
