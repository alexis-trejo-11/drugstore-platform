package microservice.user_service.auth.core.ports.input;

import microservice.user_service.auth.core.application.dto.LoginDTO;
import microservice.user_service.auth.core.application.dto.SessionResponse;
import microservice.user_service.auth.core.application.dto.SignupDTO;
import microservice.user_service.utils.response.Result;

import java.util.UUID;

public interface AuthUseCases {
    Result<UUID> register(SignupDTO signupDTO);
    Result<SessionResponse> login(LoginDTO loginDTO);

    Result<Void> changePassword(UUID userId, String currentPassword, String newPassword);
    void resetPassword(UUID userId, String newPassword);
    void activateAccount(UUID userId, String activationCode);

    void enableTwoFactorAuthentication(UUID userId);
    void disableTwoFactorAuthentication(UUID userId);
    SessionResponse twoFactorLogin(UUID userId, String twoFactorCode);

    void sendValidationCode(UUID userId);

    void logout(String token ,UUID userId);
    void logoutAllSessions(UUID userId);
    SessionResponse refreshSession(UUID userId, String refreshToken);

}
