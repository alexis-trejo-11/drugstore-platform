package microservice.auth.app.auth.core.application.usecase;

import org.springframework.stereotype.Service;

import libs_kernel.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.auth.app.auth.core.application.command.LoginCommand;
import microservice.auth.app.auth.core.application.command.LogoutAllCommand;
import microservice.auth.app.auth.core.application.command.LogoutCommand;
import microservice.auth.app.auth.core.application.command.OAuth2LoginCommand;
import microservice.auth.app.auth.core.application.command.RefreshAccessTokenCommand;
import microservice.auth.app.auth.core.application.command.SignupCommand;
import microservice.auth.app.auth.core.application.command.TwoFactorLoginCommand;
import microservice.auth.app.auth.core.application.result.SessionResult;
import microservice.auth.app.auth.core.domain.valueobjects.UserId;
import microservice.auth.app.auth.core.ports.input.AuthUseCases;
import microservice.auth.app.auth.core.ports.input.LogoutUseCases;

/**
 * AuthUseCasesImpl - Orchestrator for all authentication use cases
 * This is the entry point for the authentication bounded context, delegating to
 * specific use case services
 * Following DDD principles, this acts as an ApplicationService that coordinates
 * domain logic
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthUseCasesImpl implements AuthUseCases, LogoutUseCases {

  private final RegisterUseCase registerUseCase;
  private final LoginUseCase loginUseCase;
  private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
  private final LogoutUseCase logoutUseCase;
  private final LogoutAllUseCase logoutAllUseCase;

  /**
   * SignUp use case - Register a new user
   *
   * @param command the signup command containing user registration details
   * @return Result with the newly created user ID
   */
  @Override
  public Result<UserId> signUp(SignupCommand command) {
    log.info("AuthUseCases: Executing signup use case for email: {}",
        command.email().value());
    return registerUseCase.execute(command);
  }

  /**
   * Login use case - Authenticate user and create session
   *
   * @param command the login command containing email/phone and password
   * @return SessionResult containing JWT tokens
   */
  @Override
  public SessionResult login(LoginCommand command) {
    log.info("AuthUseCases: Executing login use case for identifier: {}",
        maskIdentifier(command.identifier()));
    return loginUseCase.execute(command);
  }

  /**
   * Refresh access token use case - Generate new access token using refresh token
   *
   * @param command the refresh access token command containing the refresh token
   * @return SessionResult containing the new access token
   */
  @Override
  public SessionResult refreshAccessToken(RefreshAccessTokenCommand command) {
    log.info("AuthUseCases: Executing refresh access token use case");
    return refreshAccessTokenUseCase.execute(command);
  }

  /**
   * Logout use case - Invalidate single session/refresh token
   *
   * @param command the logout command containing the refresh token
   */
  @Override
  public void logout(LogoutCommand command) {
    log.info("AuthUseCases: Executing logout use case");
    logoutUseCase.execute(command);
  }

  /**
   * Logout all use case - Invalidate all sessions for a user
   *
   * @param command the logout all command containing the user ID
   */
  @Override
  public void logoutAll(LogoutAllCommand command) {
    log.info("AuthUseCases: Executing logout all use case for user: {}",
        command.userId());
    logoutAllUseCase.execute(command);
  }

  /**
   * Helper method to mask sensitive data in logs
   *
   * @param identifier email or phone number
   * @return masked identifier
   */
  private String maskIdentifier(String identifier) {
    if (identifier == null || identifier.length() < 4) {
      return "***";
    }
    return identifier.substring(0, 3) + "***";
  }

  @Override
  public SessionResult oauth2Login(OAuth2LoginCommand command) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public SessionResult twoFactorLogin(TwoFactorLoginCommand command) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
