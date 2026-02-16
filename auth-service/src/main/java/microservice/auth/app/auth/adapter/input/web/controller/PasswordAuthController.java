package microservice.auth.app.auth.adapter.input.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.response.ResponseWrapper;
import microservice.auth.app.auth.adapter.input.web.dto.input.ChangePasswordRequest;
import microservice.auth.app.auth.adapter.input.web.dto.input.ForgotPasswordRequest;
import microservice.auth.app.auth.adapter.input.web.dto.input.ResetPasswordRequest;
import microservice.auth.app.auth.adapter.input.web.dto.input.ValidateResetTokenRequest;
import microservice.auth.app.auth.core.application.command.password.ChangePasswordCommand;
import microservice.auth.app.auth.core.application.command.password.ForgotPasswordCommand;
import microservice.auth.app.auth.core.application.command.password.ResetPasswordCommand;
import microservice.auth.app.auth.core.application.command.password.ValidateResetTokenCommand;
import microservice.auth.app.auth.core.ports.input.PasswordUseCases;

@RestController
@RequestMapping("/api/v2/auth/password")
public class PasswordAuthController {
    private final PasswordUseCases passwordUseCases;

    @Autowired
    public PasswordAuthController(PasswordUseCases passwordUseCases) {
        this.passwordUseCases = passwordUseCases;
    }

    @PostMapping("/forgot")
    @RateLimit(profile = RateLimitProfile.SENSITIVE)
    public ResponseWrapper<Void> forgotPassword(
            @RequestBody @Valid @NotNull ForgotPasswordRequest request) {
        ForgotPasswordCommand command = request.toCommand();
        passwordUseCases.forgotPassword(command);

        return ResponseWrapper.success("Password reset email sent successfully");
    }

    @PostMapping("/validate-token")
    @RateLimit(profile = RateLimitProfile.SENSITIVE)
    public ResponseWrapper<Void> validateResetToken(
            @RequestBody @Valid @NotNull ValidateResetTokenRequest request) {

        ValidateResetTokenCommand command = request.toCommand();
        passwordUseCases.validateResetToken(command);

        return ResponseWrapper.success("Reset token is valid");
    }

    @PostMapping("/reset")
    @RateLimit(profile = RateLimitProfile.SENSITIVE)
    public ResponseWrapper<Void> resetPassword(
            @RequestBody @Valid @NotNull ResetPasswordRequest request) {

        ResetPasswordCommand command = request.toCommand();
        passwordUseCases.resetPassword(command);

        return ResponseWrapper.success("Password reset successfully");
    }

    @PutMapping("/change")
    @RateLimit(profile = RateLimitProfile.SENSITIVE)
    public ResponseWrapper<Void> changePassword(
            @RequestAttribute("userId") String userId,
            @RequestBody @Valid @NotNull ChangePasswordRequest request) {

        ChangePasswordCommand command = request.toCommand(userId);
        passwordUseCases.changePassword(command);

        return ResponseWrapper.success(null, "Password changed successfully");
    }
}