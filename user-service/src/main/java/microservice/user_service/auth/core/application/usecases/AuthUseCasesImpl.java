package microservice.user_service.auth.core.application.usecases;

import lombok.RequiredArgsConstructor;
import microservice.user_service.auth.core.application.dto.LoginDTO;
import microservice.user_service.auth.core.application.dto.RegisterDTO;
import microservice.user_service.auth.core.application.dto.SessionResponse;
import microservice.user_service.auth.core.application.usecases.login.LoginUseCase;
import microservice.user_service.auth.core.application.usecases.login.RefreshSessionUseCase;
import microservice.user_service.auth.core.application.usecases.logout.LogoutAllUseCase;
import microservice.user_service.auth.core.application.usecases.logout.LogoutUseCase;
import microservice.user_service.auth.core.application.usecases.password.ChangePasswordUseCase;
import microservice.user_service.auth.core.application.usecases.register.ActivateUserUseCase;
import microservice.user_service.auth.core.application.usecases.register.RegisterUseCase;
import microservice.user_service.auth.core.application.usecases.twoFa.Disable2FAUseCase;
import microservice.user_service.auth.core.application.usecases.twoFa.Enable2FAUseCase;
import microservice.user_service.auth.core.ports.input.AuthUseCases;
import microservice.user_service.utils.response.Result;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthUseCasesImpl implements AuthUseCases {
    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final LogoutAllUseCase logoutAllUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshSessionUseCase refreshSessionUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ActivateUserUseCase activateUserUseCase;
    private final Enable2FAUseCase enable2FAUseCase;
    private final Disable2FAUseCase disable2FAUseCase;

    @Override
    public Result<UUID> register(RegisterDTO signupDTO) {
        return registerUseCase.execute(signupDTO);
    }

    @Override
    public Result<SessionResponse> login(LoginDTO loginDTO) {
        return loginUseCase.execute(loginDTO);
    }

    @Override
    public Result<Void> changePassword(UUID userId, String currentPassword, String newPassword) {
        return changePasswordUseCase.execute(userId, currentPassword, newPassword);
    }

    @Override
    public void resetPassword(UUID userId, String newPassword) {

    }

    @Override
    public void activateAccount(UUID userId, String activationCode) {
        activateUserUseCase.execute(userId, activationCode);
    }

    @Override
    public void enableTwoFactorAuthentication(UUID userId) {
        enable2FAUseCase.execute(userId);
    }

    @Override
    public void disableTwoFactorAuthentication(UUID userId) {
        disable2FAUseCase.execute(userId);
    }

    @Override
    public void sendValidationCode(UUID userId) {

    }

    @Override
    public SessionResponse twoFactorLogin(UUID userId, String twoFactorCode) {
        return null;
    }

    @Override
    public void logout(String token, UUID userId) {
        logoutUseCase.execute(token, userId);
    }

    @Override
    public void logoutAllSessions(UUID userId) {
        logoutAllUseCase.execute(userId);
    }

    @Override
    public SessionResponse refreshSession(UUID userId, String refreshToken) {
        return refreshSessionUseCase.execute(userId, refreshToken);
    }
}
