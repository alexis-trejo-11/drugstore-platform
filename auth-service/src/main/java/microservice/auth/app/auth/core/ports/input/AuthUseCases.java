package microservice.auth.app.auth.core.ports.input;

import libs_kernel.response.Result;
import microservice.auth.app.auth.core.application.command.LoginCommand;
import microservice.auth.app.auth.core.application.command.OAuth2LoginCommand;
import microservice.auth.app.auth.core.application.command.RefreshAccessTokenCommand;
import microservice.auth.app.auth.core.application.command.SignupCommand;
import microservice.auth.app.auth.core.application.command.TwoFactorLoginCommand;
import microservice.auth.app.auth.core.application.result.SessionResult;
import microservice.auth.app.auth.core.domain.valueobjects.UserId;

public interface AuthUseCases {
  Result<UserId> signUp(SignupCommand command);

  SessionResult login(LoginCommand command);

  SessionResult oauth2Login(OAuth2LoginCommand command);

  SessionResult twoFactorLogin(TwoFactorLoginCommand command);

  SessionResult refreshAccessToken(RefreshAccessTokenCommand command);
}
