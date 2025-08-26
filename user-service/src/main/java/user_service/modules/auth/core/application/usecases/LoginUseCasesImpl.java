package user_service.modules.auth.core.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.application.dto.LoginDTO;
import user_service.modules.auth.core.application.dto.SessionResponse;
import user_service.modules.auth.core.application.usecases.login.LocalLoginUseCase;
import user_service.modules.auth.core.application.usecases.login.RefreshSessionUseCase;
import user_service.modules.auth.core.application.usecases.twoFa.TwoFaLoginUseCase;
import user_service.modules.auth.core.ports.input.LoginUseCases;
import user_service.utils.response.Result;

@Service
@RequiredArgsConstructor
public class LoginUseCasesImpl implements LoginUseCases {
    private final LocalLoginUseCase loginUseCase;
    private final TwoFaLoginUseCase twoFaLoginUseCase;
    private final RefreshSessionUseCase refreshSessionUseCase;

    @Override
    public Result<SessionResponse> login(LoginDTO loginDTO) {
        return loginUseCase.execute(loginDTO);
    }

    @Override
    public Result<SessionResponse> twoFactorLogin(UUID userId, String twoFactorCode) {
        return twoFaLoginUseCase.execute(userId, twoFactorCode);
    }

    @Override
    public SessionResponse refreshSession(UUID userId, String refreshToken) {
        return refreshSessionUseCase.execute(userId, refreshToken);
    }

}
