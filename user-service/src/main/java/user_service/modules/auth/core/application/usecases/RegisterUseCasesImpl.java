package user_service.modules.auth.core.application.usecases;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.application.dto.RegisterDTO;
import user_service.modules.auth.core.application.usecases.register.ActivateUserUseCase;
import user_service.modules.auth.core.application.usecases.register.RegisterUseCase;
import user_service.modules.auth.core.ports.input.RegisterUseCases;
import user_service.utils.response.Result;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUseCasesImpl implements RegisterUseCases {

    private final RegisterUseCase registerUseCase;
    private final ActivateUserUseCase activateUserUseCase;

    @Override
    public Result<UUID> register(RegisterDTO signupDTO) {
        return registerUseCase.execute(signupDTO);
    }

    @Override
    public void activateAccount(UUID userId, String activationCode) {
        activateUserUseCase.execute(userId, activationCode);
    }

    @Override
    public void sendValidationCode(UUID userId) {
        throw new UnsupportedOperationException("Unimplemented method 'sendValidationCode'");
    }
}
