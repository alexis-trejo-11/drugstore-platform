package user_service.modules.auth.core.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.application.usecases.twoFa.Disable2FAUseCase;
import user_service.modules.auth.core.application.usecases.twoFa.Enable2FAUseCase;
import user_service.modules.auth.core.ports.input.TwoFaUseCases;

@Service
@RequiredArgsConstructor
public class TwoFaConfigUseCaseImpl implements TwoFaUseCases {
    private final Enable2FAUseCase enable2FAUseCase;
    private final Disable2FAUseCase disable2FAUseCase;

    @Override
    public void enableTwoFactorAuth(UUID userId) {
        enable2FAUseCase.execute(userId);
    }

    @Override
    public void disableTwoFactorAuth(UUID userId) {
        disable2FAUseCase.execute(userId);
    }

    @Override
    public void sendValidationCode(UUID userId) {
        throw new UnsupportedOperationException("Unimplemented method 'sendValidationCode'");
    }

}
