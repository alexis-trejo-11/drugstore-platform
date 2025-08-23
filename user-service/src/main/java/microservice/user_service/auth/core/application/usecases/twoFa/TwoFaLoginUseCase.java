package microservice.user_service.auth.core.application.usecases.twoFa;

import lombok.RequiredArgsConstructor;
import microservice.user_service.auth.core.application.SessionService;
import microservice.user_service.auth.core.application.dto.LoginMetadata;
import microservice.user_service.auth.core.application.dto.SessionResponse;
import microservice.user_service.auth.core.domain.session.Session;
import microservice.user_service.users.core.domain.exceptions.UserNotFoundError;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.ports.output.UserRepository;
import microservice.user_service.utils.response.Result;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TwoFaLoginUseCase {
    private UserRepository userRepository;
    private final SessionService sessionService;

    public Result<SessionResponse> execute(UUID userId, String twoFaCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundError(userId));

        user.validateNotDisabled();
        if (!user.isTwoFactorEnabled()) {
            throw new IllegalArgumentException("Two-factor authentication is not enabled for this user.");
        }

        // TODO: Implement actual 2FA code verification logic

        userRepository.updateLastLogin(user.getId());
        Session session = sessionService.createUserSession(user, LoginMetadata.empty());
        SessionResponse sessionResponse = SessionResponse.from(session);
        return Result.success(sessionResponse, "Login successful");

    }
}
