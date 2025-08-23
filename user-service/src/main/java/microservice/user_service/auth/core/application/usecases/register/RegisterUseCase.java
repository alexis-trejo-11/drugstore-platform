package microservice.user_service.auth.core.application.usecases.register;

import lombok.RequiredArgsConstructor;
import microservice.user_service.auth.core.application.dto.SignupDTO;
import microservice.user_service.utils.response.Result;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUseCase {

    public Result<UUID> execute(SignupDTO dto) {
        return Result.error(("This use case is not implemented yet. Please check back later."), 500);
    }
}
