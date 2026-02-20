package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.DisableTwoFactorCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.EnableTwoFactorCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.SendValidationCodeCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.VerifyTwoFactorCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.TwoFactorQRResult;

public interface TwoFaConfigUseCases {
	TwoFactorQRResult enableTwoFactorAuth(EnableTwoFactorCommand command);
	void disableTwoFactorAuth(DisableTwoFactorCommand command);
	void sendValidationCode(SendValidationCodeCommand command);
	void verifyTwoFactorCode(VerifyTwoFactorCommand command);
}