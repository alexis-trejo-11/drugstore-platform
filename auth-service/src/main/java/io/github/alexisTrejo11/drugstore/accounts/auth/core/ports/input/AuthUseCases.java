package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.LoginCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.RefreshAccessTokenCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.SignupCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.TwoFactorLoginCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SessionPayload;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SignUpResult;

public interface AuthUseCases {
	SignUpResult signUp(SignupCommand command);

  SessionPayload login(LoginCommand command);

  SessionPayload twoFactorLogin(TwoFactorLoginCommand command);

  SessionPayload refreshAccessToken(RefreshAccessTokenCommand command);
}
