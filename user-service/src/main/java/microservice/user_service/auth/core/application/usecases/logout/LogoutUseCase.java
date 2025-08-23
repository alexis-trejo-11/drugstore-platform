package microservice.user_service.auth.core.application.usecases.logout;

import lombok.RequiredArgsConstructor;
import microservice.user_service.auth.core.application.SessionService;
import microservice.user_service.users.core.domain.exceptions.UserNotFoundError;
import microservice.user_service.users.core.ports.output.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {
    private final SessionService sessionService;
    private final UserRepository userRepository;

    public void execute(String token, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundError(userId);
        }

        sessionService.deleteUserSession(token, userId);
    }
}
