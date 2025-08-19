package microservice.auth.app.application.usecase;

import lombok.RequiredArgsConstructor;
import microservice.auth.app.application.command.SignupCommand;
import microservice.auth.app.application.events.UserRegisteredEvent;
import microservice.auth.app.application.result.SignUpResult;
import microservice.auth.app.core.exceptions.UserAlreadyExistsError;
import microservice.auth.app.core.models.User;
import microservice.auth.app.core.ports.input.EventPublisher;
import microservice.auth.app.core.ports.input.UserServiceClient;
import microservice.auth.app.core.ports.output.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SingUpUseCase {
    private final EventPublisher eventPublisher;
    private final UserServiceClient serviceClient;
    private final UserRepository userRepository;

    public SignUpResult execute(SignupCommand command) {
        validateUserUniqueness(command);

        User newUser = createUser(command);
        userRepository.save(newUser);

        publishSignupEvent(newUser);
        return SignUpResult.success(newUser.getId());
    }

    private void validateUserUniqueness(SignupCommand command) {
        if (!serviceClient.isEmailUnique(command.getEmail().value())) {
            throw new UserAlreadyExistsError("Email already exists: " + command.getEmail());
        }
        if (!serviceClient.isPhoneUnique(command.getPhone().value())) {
            throw new UserAlreadyExistsError("Phone number already exists: " + command.getPhone());
        }
    }

    private User createUser(SignupCommand command) {
        return User.builder()
                .email(command.getEmail())
                .phoneNumber(command.getPhone())
                .password(command.getPassword())
                .build();
    }

    public void publishSignupEvent(User user) {
        UserRegisteredEvent event = new UserRegisteredEvent(
                user.getId(),
                user.getEmail(),
                user.getPhoneNumber()
        );
        eventPublisher.publishUserRegistered(event);
    }
}


