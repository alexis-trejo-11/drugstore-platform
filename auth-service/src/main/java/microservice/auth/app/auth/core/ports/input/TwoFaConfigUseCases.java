package microservice.auth.app.auth.core.ports.input;


public interface TwoFaConfigUseCases {
	TwoFactorQRResult enableTwoFactorAuth(EnableTwoFactorCommand command);
	void disableTwoFactorAuth(DisableTwoFactorCommand command);
	void sendValidationCode(SendValidationCodeCommand command);
	void verifyTwoFactorCode(VerifyTwoFactorCommand command);
}