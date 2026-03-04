package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.controller;

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
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input.ChangePasswordRequest;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input.ForgotPasswordRequest;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input.ResetPasswordRequest;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input.ValidateResetTokenRequest;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ChangePasswordCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ForgotPasswordCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ResetPasswordCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ValidateResetTokenCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.PasswordUseCases;

@Slf4j
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
    log.info("Password reset request received");

    ForgotPasswordCommand command = request.toCommand("Unknown IP");
    passwordUseCases.forgotPassword(command);

    log.info("Password reset email sent successfully");
    return ResponseWrapper.success("Password reset email sent successfully");
  }

  @PostMapping("/validate-token")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  public ResponseWrapper<Void> validateResetToken(
      @RequestBody @Valid @NotNull ValidateResetTokenRequest request) {
    log.debug("Validating password reset token");

    ValidateResetTokenCommand command = request.toCommand();
    passwordUseCases.validateResetToken(command);

    log.debug("Reset token validated successfully");
    return ResponseWrapper.success("Reset token is valid");
  }

  @PostMapping("/reset")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  public ResponseWrapper<Void> resetPassword(
      @RequestBody @Valid @NotNull ResetPasswordRequest request) {
    log.info("Password reset requested");

    ResetPasswordCommand command = request.toCommand();
    passwordUseCases.resetPassword(command);

    log.info("Password reset successfully");
    return ResponseWrapper.success("Password reset successfully");
  }

  @PutMapping("/change")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  public ResponseWrapper<Void> changePassword(
      @RequestAttribute("userId") String userId,
      @RequestBody @Valid @NotNull ChangePasswordRequest request) {
    log.info("Password change request for user: {}", userId);

    ChangePasswordCommand command = request.toCommand(userId);
    passwordUseCases.changePassword(command);

    log.info("Password changed successfully for user: {}", userId);
    return ResponseWrapper.success(null, "Password changed successfully");
  }
}