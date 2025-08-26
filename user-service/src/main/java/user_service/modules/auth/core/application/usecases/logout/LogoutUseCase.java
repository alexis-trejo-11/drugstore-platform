package user_service.modules.auth.core.application.usecases.logout;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.application.helping_services.SessionService;
import user_service.modules.users.core.domain.exceptions.UserNotFoundError;
import user_service.modules.users.core.ports.output.UserRepository;
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
