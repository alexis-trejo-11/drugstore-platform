package io.github.alexisTrejo11.drugstore.users.app.user.core.application.handlers.command;

import io.github.alexisTrejo11.drugstore.users.app.user.core.application.command.CreateUserCommand;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input.CommandHandler;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.mappers.UserMapper;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.result.CommandResult;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.exceptions.EmailAlreadyTakenError;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.exceptions.PhoneAlreadyTakenError;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.output.EventPublisher;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.output.UserRepository;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.service.PasswordValidator;
import io.github.alexisTrejo11.drugstore.users.app.user.adapter.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateUserCommandHandler implements CommandHandler<CreateUserCommand> {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher eventPublisher;

    public CreateUserCommandHandler(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder, EventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CommandResult handle(CreateUserCommand command) {
        validateUniqueFields(command);
        PasswordValidator.validatePasswordStrength(command.password());

        String hashedPassword = passwordEncoder.hashPassword(command.password());
        User newUser = userMapper.fromCreateCommand(command, hashedPassword);
        User userSaved = userRepository.save(newUser);

        eventPublisher.publish(userMapper.toEvent(newUser));
        return CommandResult.success("User created successfully", userSaved.getId());
    }

    private void validateUniqueFields(CreateUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyTakenError(command.email());
        }

        if (command.phoneNumber() != null) {
            if (userRepository.existsByPhoneNumber(command.phoneNumber())) {
                throw new PhoneAlreadyTakenError(command.phoneNumber());
            }
        }
    }
}