package user_service.modules.users.core.application.handlers.command;

import user_service.modules.users.core.application.command.CreateUserCommand;
import user_service.modules.users.core.ports.input.CommandHandler;
import user_service.modules.users.core.application.mappers.UserMapper;
import user_service.modules.users.core.application.result.CommandResult;
import user_service.modules.users.core.domain.exceptions.EmailAlreadyTakenError;
import user_service.modules.users.core.domain.exceptions.PhoneAlreadyTakenError;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.output.EventPublisher;
import user_service.modules.users.core.ports.output.UserRepository;
import user_service.modules.users.core.domain.service.PasswordValidator;
import user_service.utils.password.PasswordEncoder;
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