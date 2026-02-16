package microservice.auth.app.auth.core.ports.input;

import microservice.auth.app.auth.core.application.command.password.ChangePasswordCommand;
import microservice.auth.app.auth.core.application.command.password.ForgotPasswordCommand;
import microservice.auth.app.auth.core.application.command.password.ResetPasswordCommand;
import microservice.auth.app.auth.core.application.command.password.ValidateResetTokenCommand;

public interface PasswordUseCases {
    void forgotPassword(ForgotPasswordCommand command);
    void validateResetToken(ValidateResetTokenCommand command);
    void resetPassword(ResetPasswordCommand command);
		void changePassword(ChangePasswordCommand command);
}