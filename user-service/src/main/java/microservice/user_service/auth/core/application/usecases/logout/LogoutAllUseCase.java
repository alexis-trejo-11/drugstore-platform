package microservice.user_service.auth.core.application.usecases.logout;

import microservice.user_service.auth.core.domain.repositories.SessionRepository;
import microservice.user_service.users.core.domain.exceptions.UserNotFoundError;
import microservice.user_service.users.core.ports.output.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LogoutAllUseCase {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Autowired
    public LogoutAllUseCase(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    public void execute(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundError(userId);
        }

        sessionRepository.deactivateAllUserSessions(userId.toString());
    }
}
