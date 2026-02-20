package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ChangePasswordCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ForgotPasswordCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ResetPasswordCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ValidateResetTokenCommand;

public interface PasswordUseCases {
    void forgotPassword(ForgotPasswordCommand command);
    void validateResetToken(ValidateResetTokenCommand command);
    void resetPassword(ResetPasswordCommand command);
		void changePassword(ChangePasswordCommand command);
}