package microservice.auth_service.app.auth.core.ports.input;

import libs_kernel.response.Result;
import microservice.auth_service.app.auth.core.application.command.SignupCommand;
import microservice.auth_service.app.auth.core.domain.valueobjects.UserId;

public interface RegisterUseCases {
    Result<UserId> register(SignupCommand command);
    void activateAccount(String activationCode);
}
