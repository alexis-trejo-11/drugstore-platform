package microservice.auth.app.auth.adapter.input.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.response.ResponseWrapper;
import microservice.auth.app.auth.adapter.input.web.dto.input.ForgotPasswordRequest;
import microservice.auth.app.auth.adapter.input.web.dto.input.ResetPasswordRequest;
import microservice.auth.app.auth.adapter.input.web.dto.input.ValidateResetTokenRequest;
import microservice.auth.app.auth.core.application.command.ForgotPasswordCommand;
import microservice.auth.app.auth.core.application.command.ResetPasswordCommand;
import microservice.auth.app.auth.core.application.command.ValidateResetTokenCommand;
import microservice.auth.app.auth.core.ports.input.PasswordResetUseCases;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/auth/password")
public class PasswordResetController {
    private final PasswordResetUseCases passwordResetUseCases;

    @Autowired
    public PasswordResetController(PasswordResetUseCases passwordResetUseCases) {
        this.passwordResetUseCases = passwordResetUseCases;
    }

    @PostMapping("/forgot")
    public ResponseEntity<ResponseWrapper<Void>> forgotPassword(
            @RequestBody @Valid @NotNull ForgotPasswordRequest request) {
        ForgotPasswordCommand command = request.toCommand();
        passwordResetUseCases.forgotPassword(command);

        return ResponseEntity.ok(
                ResponseWrapper.success(null, "Password reset email sent successfully")
        );
    }

    @PostMapping("/validate-token")
    public ResponseEntity<ResponseWrapper<Void>> validateResetToken(
            @RequestBody @Valid @NotNull ValidateResetTokenRequest request) {
        ValidateResetTokenCommand command = request.toCommand();
        passwordResetUseCases.validateResetToken(command);

        return ResponseEntity.ok(
                ResponseWrapper.success(null, "Reset token is valid")
        );
    }

    @PostMapping("/reset")
    public ResponseEntity<ResponseWrapper<Void>> resetPassword(
            @RequestBody @Valid @NotNull ResetPasswordRequest request) {
        ResetPasswordCommand command = request.toCommand();
        passwordResetUseCases.resetPassword(command);

        return ResponseEntity.ok(
                ResponseWrapper.success(null, "Password reset successfully")
        );
    }
}