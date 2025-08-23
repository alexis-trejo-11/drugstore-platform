package microservice.user_service.auth.core.application.usecases.login;

import lombok.RequiredArgsConstructor;
import microservice.user_service.auth.core.application.SessionService;
import microservice.user_service.auth.core.application.dto.SessionResponse;
import microservice.user_service.auth.core.domain.session.Session;
import microservice.user_service.users.core.domain.exceptions.UserNotFoundError;
import microservice.user_service.users.core.ports.output.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshSessionUseCase {
    private final SessionService sessionService;
    private final UserRepository userRepository;

    public SessionResponse execute(UUID userId, String token) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundError(userId);
        }

        Session refreshUserSession = sessionService.refreshUserSession(token, userId);
        return SessionResponse.from(refreshUserSession);
    }
}
