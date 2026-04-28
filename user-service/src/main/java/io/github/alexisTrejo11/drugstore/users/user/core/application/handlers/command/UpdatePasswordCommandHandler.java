package io.github.alexisTrejo11.drugstore.users.user.core.application.handlers.command;

import io.github.alexisTrejo11.drugstore.users.user.core.application.command.UpdatePasswordCommand;
import io.github.alexisTrejo11.drugstore.users.user.core.application.result.CommandResult;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.input.CommandHandler;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.exceptions.UserNotFoundError;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.output.UserRepository;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.service.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.alexisTrejo11.drugstore.users.user.adapter.password.PasswordEncoder;
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

        return CommandResult.success("User updated successfully", user);
    }
}
