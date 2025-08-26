package user_service.modules.auth.core.ports.input;

import user_service.modules.auth.core.application.dto.RegisterDTO;
import user_service.utils.response.Result;

import java.util.UUID;

public interface RegisterUseCases {
    Result<UUID> register(RegisterDTO signupDTO);

    void activateAccount(UUID userId, String activationCode);

    void sendValidationCode(UUID userId);
}
