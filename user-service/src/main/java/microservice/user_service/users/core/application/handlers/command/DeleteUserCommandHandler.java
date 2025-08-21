package microservice.user_service.users.core.application.handlers.command;

import microservice.user_service.users.core.application.command.DeleteUserCommand;
import microservice.user_service.users.core.application.dto.CommandResult;
import microservice.user_service.users.core.ports.input.CommandHandler;
import microservice.user_service.users.core.domain.exceptions.UserNotFoundError;
import microservice.user_service.users.core.ports.output.UserRepository;
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
