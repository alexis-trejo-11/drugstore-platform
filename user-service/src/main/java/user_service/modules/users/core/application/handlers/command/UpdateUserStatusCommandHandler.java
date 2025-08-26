package user_service.modules.users.core.application.handlers.command;

import user_service.modules.users.core.application.command.UpdateUserStatusCommand;
import user_service.modules.users.core.application.dto.CommandResult;
import user_service.modules.users.core.ports.input.CommandHandler;
import user_service.modules.users.core.domain.exceptions.UserNotFoundError;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.output.UserRepository;
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
