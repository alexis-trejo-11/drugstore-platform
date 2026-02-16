package microservice.auth.app.auth.core.ports.input;

import microservice.auth.app.auth.core.application.command.twoFa.DisableTwoFactorCommand;
import microservice.auth.app.auth.core.application.command.twoFa.EnableTwoFactorCommand;
import microservice.auth.app.auth.core.application.command.twoFa.SendValidationCodeCommand;
import microservice.auth.app.auth.core.application.command.twoFa.VerifyTwoFactorCommand;
import microservice.auth.app.auth.core.application.result.TwoFactorQRResult;

public interface TwoFaConfigUseCases {
	TwoFactorQRResult enableTwoFactorAuth(EnableTwoFactorCommand command);
	void disableTwoFactorAuth(DisableTwoFactorCommand command);
	void sendValidationCode(SendValidationCodeCommand command);
	void verifyTwoFactorCode(VerifyTwoFactorCommand command);
}