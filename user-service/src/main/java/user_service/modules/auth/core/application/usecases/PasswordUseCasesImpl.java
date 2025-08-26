package user_service.modules.auth.core.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.application.usecases.password.ChangePasswordUseCase;
import user_service.modules.auth.core.application.usecases.password.ResetPasswordUseCase;
import user_service.modules.auth.core.ports.input.PasswordUseCases;
import user_service.utils.response.Result;

@Service
@RequiredArgsConstructor
public class PasswordUseCasesImpl implements PasswordUseCases {
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    @Override
    public Result<Void> changePassword(UUID userId, String currentPassword, String newPassword) {
        return changePasswordUseCase.execute(userId, currentPassword, newPassword);
    }

    @Override
    public void resetPassword(UUID userId, String validationToken, String newPassword) {
        resetPasswordUseCase.execute(userId, validationToken, newPassword);
    }
}
