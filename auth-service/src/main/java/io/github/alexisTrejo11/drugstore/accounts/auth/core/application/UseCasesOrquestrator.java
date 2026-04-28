package io.github.alexisTrejo11.drugstore.accounts.auth.core.application;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.password.ChangePasswordUseCase;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.password.ForgotPasswordUseCase;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.password.ResetPasswordUseCase;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.token.ActivateAccountUseCase;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.token.SendValidationCodeUseCase;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.token.ValidateResetTokenUseCase;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.twofa.DisableTwoFactorAuthUseCase;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.twofa.EnableTwoFactorAuthUseCase;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.twofa.TwoFactorLoginUseCase;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.twofa.VerifyTwoFactorCodeUseCase;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.LogoutAllCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.LogoutCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.RefreshAccessTokenCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.SignupCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.LoginCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.TwoFactorLoginCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ChangePasswordCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ForgotPasswordCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ResetPasswordCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ValidateResetTokenCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.DisableTwoFactorCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.EnableTwoFactorCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.SendValidationCodeCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.VerifyTwoFactorCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SessionPayload;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SignUpResult;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.TwoFactorQRResult;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.LoginUseCase;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.logout.LogoutAllUseCase;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.logout.LogoutUseCase;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.token.RefreshAccessTokenUseCase;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.RegisterUseCase;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.AuthUseCases;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.LogoutUseCases;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.PasswordUseCases;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.RegisterUseCases;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TwoFaConfigUseCases;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
	private final TwoFactorLoginUseCase twoFactorLoginUseCase;
	private final ForgotPasswordUseCase forgotPasswordUseCase;
	private final ValidateResetTokenUseCase validateResetTokenUseCase;
	private final ResetPasswordUseCase resetPasswordUseCase;
	private final ChangePasswordUseCase changePasswordUseCase;
	private final ActivateAccountUseCase activateAccountUseCase;
	private final EnableTwoFactorAuthUseCase enableTwoFactorAuthUseCase;
	private final DisableTwoFactorAuthUseCase disableTwoFactorAuthUseCase;
	private final SendValidationCodeUseCase sendValidationCodeUseCase;
	private final VerifyTwoFactorCodeUseCase verifyTwoFactorCodeUseCase;

  @Override
  public SignUpResult register(SignupCommand command) {
    log.info("AuthUseCases: Executing signup use case for email: {}",
        command.email().value());
    return registerUseCase.execute(command);
  }

  @Override
  public SessionPayload login(LoginCommand command) {
    log.info("AuthUseCases: Executing login use case for identifier: {}",
        maskIdentifier(command.identifier()));
    return loginUseCase.execute(command);
  }

  @Override
  public SessionPayload refreshAccessToken(RefreshAccessTokenCommand command) {
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
  public SessionPayload twoFactorLogin(TwoFactorLoginCommand command) {
		log.info("AuthUseCases: Executing 2FA login use case for user: {}", command.email());
		return twoFactorLoginUseCase.execute(command);
  }

  @Override
  public void forgotPassword(ForgotPasswordCommand command) {
    log.info("AuthUseCases: Executing forgot password use case for email: {}", command.email());
		forgotPasswordUseCase.execute(command);
  }

  @Override
  public void validateResetToken(ValidateResetTokenCommand command) {
    log.info("AuthUseCases: Executing validate reset token use case");
    validateResetTokenUseCase.execute(command);
  }

  @Override
  public void resetPassword(ResetPasswordCommand command) {
    log.info("AuthUseCases: Executing reset password use case");
    resetPasswordUseCase.execute(command);
  }

  @Override
  public void changePassword(ChangePasswordCommand command) {
    log.info("AuthUseCases: Executing change password use case for user: {}", command.userId());
    changePasswordUseCase.execute(command);
  }

  @Override
  public void activateAccount(String activationCode) {
    log.info("AuthUseCases: Executing activate account use case");
    activateAccountUseCase.execute(activationCode);
  }

  @Override
  public TwoFactorQRResult enableTwoFactorAuth(EnableTwoFactorCommand command) {
    log.info("AuthUseCases: Executing enable two-factor authentication use case for user: {}", command.userId());
    return enableTwoFactorAuthUseCase.execute(command);
  }

  @Override
  public void disableTwoFactorAuth(DisableTwoFactorCommand command) {
    log.info("AuthUseCases: Executing disable two-factor authentication use case for user: {}", command.userId());
    disableTwoFactorAuthUseCase.execute(command);
  }

  @Override
  public void sendValidationCode(SendValidationCodeCommand command) {
    log.info("AuthUseCases: Executing send validation code use case for user: {}", command.userIdString());
    sendValidationCodeUseCase.execute(command);
  }

  @Override
  public void verifyTwoFactorCode(VerifyTwoFactorCommand command) {
    log.info("AuthUseCases: Executing verify two-factor code use case for user: {}", command.userId());
    verifyTwoFactorCodeUseCase.execute(command);
  }
}
