package microservice.user_service.users.core.application.handlers.command;

import microservice.user_service.users.core.application.command.UpdateUserStatusCommand;
import microservice.user_service.users.core.application.dto.CommandResult;
import microservice.user_service.users.core.application.handlers.CommandHandler;
import microservice.user_service.users.core.domain.exceptions.UserNotFoundError;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.domain.ports.output.UserRepository;
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
            case DEACTIVATE -> user.deactivate();
            default -> throw new IllegalArgumentException("Invalid action: " + command.actions());
        }

        userRepository.save(user);

        return CommandResult.success("User activated successfully.");
    }
}
