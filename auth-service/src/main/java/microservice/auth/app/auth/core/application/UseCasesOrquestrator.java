package microservice.auth.app.auth.core.application;

import org.springframework.stereotype.Service;

import libs_kernel.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.auth.app.auth.core.application.command.LogoutAllCommand;
import microservice.auth.app.auth.core.application.command.LogoutCommand;
import microservice.auth.app.auth.core.application.command.RefreshAccessTokenCommand;
import microservice.auth.app.auth.core.application.command.SignupCommand;
import microservice.auth.app.auth.core.application.command.login.LoginCommand;
import microservice.auth.app.auth.core.application.command.login.OAuth2LoginCommand;
import microservice.auth.app.auth.core.application.command.login.TwoFactorLoginCommand;
import microservice.auth.app.auth.core.application.command.password.ChangePasswordCommand;
import microservice.auth.app.auth.core.application.command.password.ForgotPasswordCommand;
import microservice.auth.app.auth.core.application.command.password.ResetPasswordCommand;
import microservice.auth.app.auth.core.application.command.password.ValidateResetTokenCommand;
import microservice.auth.app.auth.core.application.command.twoFa.DisableTwoFactorCommand;
import microservice.auth.app.auth.core.application.command.twoFa.EnableTwoFactorCommand;
import microservice.auth.app.auth.core.application.command.twoFa.SendValidationCodeCommand;
import microservice.auth.app.auth.core.application.command.twoFa.VerifyTwoFactorCommand;
import microservice.auth.app.auth.core.application.result.SessionResult;
import microservice.auth.app.auth.core.application.result.SignUpResult;
import microservice.auth.app.auth.core.application.result.TwoFactorQRResult;
import microservice.auth.app.auth.core.application.usecase.LoginUseCase;
import microservice.auth.app.auth.core.application.usecase.LogoutAllUseCase;
import microservice.auth.app.auth.core.application.usecase.LogoutUseCase;
import microservice.auth.app.auth.core.application.usecase.RefreshAccessTokenUseCase;
import microservice.auth.app.auth.core.application.usecase.RegisterUseCase;
import microservice.auth.app.auth.core.domain.valueobjects.UserId;
import microservice.auth.app.auth.core.ports.input.AuthUseCases;
import microservice.auth.app.auth.core.ports.input.LogoutUseCases;
import microservice.auth.app.auth.core.ports.input.PasswordUseCases;
import microservice.auth.app.auth.core.ports.input.RegisterUseCases;
import microservice.auth.app.auth.core.ports.input.TwoFaConfigUseCases;

/**
 * UseCasesOrquestrator - Orchestrator for all authentication use cases
 * This is the entry point for the authentication bounded context, delegating to
 * specific use case services
 * Following DDD principles, this acts as an ApplicationService that coordinates
 * domain logic
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UseCasesOrquestrator
    implements AuthUseCases, LogoutUseCases, PasswordUseCases, RegisterUseCases, TwoFaConfigUseCases {

  private final RegisterUseCase registerUseCase;
  private final LoginUseCase loginUseCase;
  private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
  private final LogoutUseCase logoutUseCase;
  private final LogoutAllUseCase logoutAllUseCase;

  @Override
  public SignUpResult signUp(SignupCommand command) {
    log.info("AuthUseCases: Executing signup use case for email: {}",
        command.email().value());
    return registerUseCase.execute(command);
  }

  @Override
  public SessionResult login(LoginCommand command) {
    log.info("AuthUseCases: Executing login use case for identifier: {}",
        maskIdentifier(command.identifier()));
    return loginUseCase.execute(command);
  }

  @Override
  public SessionResult refreshAccessToken(RefreshAccessTokenCommand command) {
    log.info("AuthUseCases: Executing refresh access token use case");
    return refreshAccessTokenUseCase.execute(command);
  }

  @Override
  public void logout(LogoutCommand command) {
    log.info("AuthUseCases: Executing logout use case");
    logoutUseCase.execute(command);
  }

  @Override
  public void logoutAll(LogoutAllCommand command) {
    log.info("AuthUseCases: Executing logout all use case for user: {}",
        command.userId());
    logoutAllUseCase.execute(command);
  }

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

  @Override
  public void forgotPassword(ForgotPasswordCommand command) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void validateResetToken(ValidateResetTokenCommand command) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void resetPassword(ResetPasswordCommand command) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void changePassword(ChangePasswordCommand command) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Result<UserId> register(SignupCommand command) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void activateAccount(String activationCode) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public TwoFactorQRResult enableTwoFactorAuth(EnableTwoFactorCommand command) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void disableTwoFactorAuth(DisableTwoFactorCommand command) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void sendValidationCode(SendValidationCodeCommand command) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void verifyTwoFactorCode(VerifyTwoFactorCommand command) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
