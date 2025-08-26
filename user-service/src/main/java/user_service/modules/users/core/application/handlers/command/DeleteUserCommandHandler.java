package user_service.modules.users.core.application.handlers.command;

import user_service.modules.users.core.application.command.DeleteUserCommand;
import user_service.modules.users.core.application.dto.CommandResult;
import user_service.modules.users.core.ports.input.CommandHandler;
import user_service.modules.users.core.domain.exceptions.UserNotFoundError;
import user_service.modules.users.core.ports.output.UserRepository;
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
