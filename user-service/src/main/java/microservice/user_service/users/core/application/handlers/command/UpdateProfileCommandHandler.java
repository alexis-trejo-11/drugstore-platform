package microservice.user_service.users.core.application.handlers.command;

import microservice.user_service.users.core.application.command.UpdateProfileCommand;
import microservice.user_service.users.core.application.dto.CommandResult;
import microservice.user_service.users.core.application.handlers.CommandHandler;
import microservice.user_service.users.core.domain.exceptions.UserNotFoundError;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.domain.ports.output.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateProfileCommandHandler implements CommandHandler<UpdateProfileCommand> {
    private final UserRepository userRepository;

    @Autowired
    public UpdateProfileCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public CommandResult handle(UpdateProfileCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundError(command.userId()));

        user.updateProfile(
            command.firstName(),
            command.lastName(),
            command.dateOfBirth(),
            command.gender()
        );

        userRepository.save(user);

        return CommandResult.success("Profile updated successfully", user.getId());
    }
}
