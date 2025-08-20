package microservice.user_service.users.core.application.handlers.command;

import microservice.user_service.users.core.application.command.UpdatePasswordCommand;
import microservice.user_service.users.core.application.dto.CommandResult;
import microservice.user_service.users.core.application.handlers.CommandHandler;
import microservice.user_service.users.core.domain.exceptions.UserNotFoundError;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.domain.ports.output.UserRepository;
import microservice.user_service.users.core.domain.service.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import microservice.user_service.utils.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UpdatePasswordCommandHandler implements CommandHandler<UpdatePasswordCommand> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UpdatePasswordCommandHandler(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CommandResult handle(UpdatePasswordCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundError(command.userId()));

        PasswordValidator.validatePasswordStrength(command.newPlainPassword());
        String hashedPassword = passwordEncoder.hashPassword(command.newPlainPassword());
        user.setHashedPassword(hashedPassword);

        userRepository.save(user);

        return CommandResult.success("User updated successfully", user);    }
}
