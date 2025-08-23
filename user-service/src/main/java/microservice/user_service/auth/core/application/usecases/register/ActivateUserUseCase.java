package microservice.user_service.auth.core.application.usecases.register;

import lombok.RequiredArgsConstructor;
import microservice.user_service.users.core.domain.exceptions.UserNotFoundError;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.ports.output.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivateUserUseCase {
    private UserRepository userRepository;

    public void execute(UUID userId, String activationCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundError(userId));

        user.activate();
        userRepository.save(user);
    }
}
