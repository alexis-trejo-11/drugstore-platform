package user_service.modules.auth.core.ports.input;

import java.util.UUID;

import user_service.modules.auth.core.application.dto.LoginDTO;
import user_service.modules.auth.core.application.dto.SessionResponse;
import user_service.utils.response.Result;

public interface LoginUseCases {
    Result<SessionResponse> login(LoginDTO loginDTO);

    Result<SessionResponse> twoFactorLogin(UUID userId, String twoFactorCode);

    SessionResponse refreshSession(UUID userId, String refreshToken);

}
