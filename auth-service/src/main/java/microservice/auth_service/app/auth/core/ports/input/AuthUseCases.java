package microservice.auth_service.app.auth.core.ports.input;

import microservice.auth_service.app.auth.core.application.command.login.LoginCommand;
import microservice.auth_service.app.auth.core.application.command.login.OAuth2LoginCommand;
import microservice.auth_service.app.auth.core.application.command.RefreshAccessTokenCommand;
import microservice.auth_service.app.auth.core.application.command.SignupCommand;
import microservice.auth_service.app.auth.core.application.command.login.TwoFactorLoginCommand;
import microservice.auth_service.app.auth.core.application.result.SessionResult;
import microservice.auth_service.app.auth.core.application.result.SignUpResult;

public interface AuthUseCases {
	SignUpResult signUp(SignupCommand command);

  SessionResult login(LoginCommand command);

  SessionResult oauth2Login(OAuth2LoginCommand command);

  SessionResult twoFactorLogin(TwoFactorLoginCommand command);

  SessionResult refreshAccessToken(RefreshAccessTokenCommand command);
}
