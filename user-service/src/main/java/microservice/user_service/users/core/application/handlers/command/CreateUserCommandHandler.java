package microservice.user_service.users.core.application.handlers.command;

import microservice.user_service.users.core.application.command.CreateUserCommand;
import microservice.user_service.users.core.application.dto.CommandResult;
import microservice.user_service.users.core.ports.input.CommandHandler;
import microservice.user_service.users.core.application.mappers.UserMapper;
import microservice.user_service.users.core.domain.exceptions.EmailAlreadyTakenError;
import microservice.user_service.users.core.domain.exceptions.PhoneAlreadyTakenError;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.ports.output.EventPublisher;
import microservice.user_service.users.core.ports.output.UserRepository;
import microservice.user_service.users.core.domain.service.PasswordValidator;
import microservice.user_service.utils.password.PasswordEncoder;
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
            PasswordEncoder passwordEncoder, EventPublisher eventPublisher
    ) {
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
        userRepository.save(newUser);

        eventPublisher.publish(userMapper.ToEvent(newUser));
        return CommandResult.success("User created successfully", newUser.getId());
    }

    private void validateUniqueFields(CreateUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyTakenError(command.email());
        }

        if (userRepository.existsByPhoneNumber(command.phoneNumber())) {
            throw new PhoneAlreadyTakenError(command.phoneNumber());
        }
    }
}