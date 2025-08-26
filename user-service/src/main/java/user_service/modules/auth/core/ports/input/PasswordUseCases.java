package user_service.modules.auth.core.ports.input;

import java.util.UUID;

import user_service.utils.response.Result;

public interface PasswordUseCases {
    Result<Void> changePassword(UUID userId, String currentPassword, String newPassword);

    void resetPassword(UUID userId, String validationToken, String newPassword);

}
