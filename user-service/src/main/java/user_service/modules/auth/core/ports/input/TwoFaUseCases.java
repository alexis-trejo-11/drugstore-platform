package user_service.modules.auth.core.ports.input;

import java.util.UUID;

public interface TwoFaUseCases {
    void enableTwoFactorAuth(UUID userId);

    void disableTwoFactorAuth(UUID userId);

    void sendValidationCode(UUID userId);
}