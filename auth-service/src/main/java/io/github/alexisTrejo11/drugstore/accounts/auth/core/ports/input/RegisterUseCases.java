package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input;

import libs_kernel.response.Result;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.SignupCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

public interface RegisterUseCases {
    Result<UserId> register(SignupCommand command);
    void activateAccount(String activationCode);
}
