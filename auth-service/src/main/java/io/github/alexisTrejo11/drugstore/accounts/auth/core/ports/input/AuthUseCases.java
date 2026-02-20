package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.LoginCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.OAuth2LoginCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.RefreshAccessTokenCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.SignupCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.TwoFactorLoginCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SessionResult;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SignUpResult;

public interface AuthUseCases {
	SignUpResult signUp(SignupCommand command);

  SessionResult login(LoginCommand command);

  SessionResult oauth2Login(OAuth2LoginCommand command);

  SessionResult twoFactorLogin(TwoFactorLoginCommand command);

  SessionResult refreshAccessToken(RefreshAccessTokenCommand command);
}
