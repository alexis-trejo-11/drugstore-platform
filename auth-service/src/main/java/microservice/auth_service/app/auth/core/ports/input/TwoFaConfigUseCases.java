package microservice.auth_service.app.auth.core.ports.input;

import microservice.auth_service.app.auth.core.application.command.twoFa.DisableTwoFactorCommand;
import microservice.auth_service.app.auth.core.application.command.twoFa.EnableTwoFactorCommand;
import microservice.auth_service.app.auth.core.application.command.twoFa.SendValidationCodeCommand;
import microservice.auth_service.app.auth.core.application.command.twoFa.VerifyTwoFactorCommand;
import microservice.auth_service.app.auth.core.application.result.TwoFactorQRResult;

public interface TwoFaConfigUseCases {
	TwoFactorQRResult enableTwoFactorAuth(EnableTwoFactorCommand command);
	void disableTwoFactorAuth(DisableTwoFactorCommand command);
	void sendValidationCode(SendValidationCodeCommand command);
	void verifyTwoFactorCode(VerifyTwoFactorCommand command);
}