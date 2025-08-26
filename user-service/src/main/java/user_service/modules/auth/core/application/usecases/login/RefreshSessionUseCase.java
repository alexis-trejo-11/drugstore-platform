package user_service.modules.auth.core.application.usecases.login;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.application.dto.SessionResponse;
import user_service.modules.auth.core.application.helping_services.SessionService;
import user_service.modules.auth.core.domain.session.Session;
import user_service.modules.users.core.domain.exceptions.UserNotFoundError;
import user_service.modules.users.core.ports.output.UserRepository;
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

        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Refresh token must be provided");
        }

        Session refreshUserSession = sessionService.refreshUserSession(token, userId);
        return SessionResponse.from(refreshUserSession);
    }
}
