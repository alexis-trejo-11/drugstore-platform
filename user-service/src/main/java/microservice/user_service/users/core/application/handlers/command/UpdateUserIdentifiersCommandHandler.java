package microservice.user_service.users.core.application.handlers.command;

import microservice.user_service.users.core.application.command.UpdateUserCommand;
import microservice.user_service.users.core.application.dto.CommandResult;
import microservice.user_service.users.core.application.handlers.CommandHandler;
import microservice.user_service.users.core.domain.exceptions.UserNotFoundError;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.domain.ports.output.UserRepository;
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
                command.email().toString(),
                command.phoneNumber().toString()
        );
        userRepository.save(user);

        return CommandResult.success("User updated successfully", user);
    }
}
