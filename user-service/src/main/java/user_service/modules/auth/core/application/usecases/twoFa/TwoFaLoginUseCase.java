package user_service.modules.auth.core.application.usecases.twoFa;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.application.dto.LoginMetadata;
import user_service.modules.auth.core.application.dto.SessionResponse;
import user_service.modules.auth.core.application.helping_services.SessionService;
import user_service.modules.auth.core.application.helping_services.TokenService;
import user_service.modules.auth.core.domain.session.Session;
import user_service.modules.users.core.domain.exceptions.UserNotFoundError;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.output.UserRepository;
import user_service.utils.response.Result;
import user_service.utils.tokens.interfaces.TokenType;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TwoFaLoginUseCase {
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final TokenService tokenService;

    public Result<SessionResponse> execute(UUID userId, String twoFaCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundError(userId));

        var result = validateLogin(user, twoFaCode);
        if (!result.isSuccess()) {
            return Result.error(result.getMessage(), result.getStatusCode());
        }

        SessionResponse sessionResponse = proccessLogin(user);
        return Result.success(sessionResponse, "Login successful");
    }

    public Result<SessionResponse> validateLogin(User user, String twoFaCode) {
        user.validateNotDisabled();
        if (!user.isTwoFactorEnabled()) {
            return Result.error("2FA is not enabled for this user", 400);
        }

        boolean isTokenValid = tokenService.validateToken(twoFaCode, TokenType.TWO_FA, user.getId());
        if (!isTokenValid) {
            return Result.error("Invalid 2FA code", 401);
        }

        return Result.success(null, "2FA code validated successfully");
    }

    public SessionResponse proccessLogin(User user) {
        userRepository.updateLastLoginAsync(user.getId());
        Session session = sessionService.createUserSession(user, LoginMetadata.empty());
        return SessionResponse.from(session);
    }
}
