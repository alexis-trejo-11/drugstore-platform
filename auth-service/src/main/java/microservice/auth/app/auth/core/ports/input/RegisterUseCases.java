package microservice.auth.app.auth.core.ports.input;

import libs_kernel.response.Result;
import microservice.auth.app.auth.core.application.command.SignupCommand;
import microservice.auth.app.auth.core.domain.valueobjects.UserId;

public interface RegisterUseCases {
    Result<UserId> register(SignupCommand command);
    void activateAccount(String activationCode);
}
