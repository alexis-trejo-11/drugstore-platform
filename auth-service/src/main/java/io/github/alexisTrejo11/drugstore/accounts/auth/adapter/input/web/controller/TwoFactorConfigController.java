package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.response.ResponseWrapper;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input.ConfirmTwoFactorSetupRequest;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.ConfirmTwoFactorSetupCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.DisableTwoFactorCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.EnableTwoFactorCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.SendValidationCodeCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.VerifyTwoFactorCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.TwoFactorQRResult;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TwoFaConfigUseCases;
import io.github.alexisTrejo11.drugstore.accounts.config.OpenApiConfig;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations.ConfirmTwoFactorSetupOperation;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations.DisableTwoFactorAuthOperation;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations.EnableTwoFactorAuthOperation;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations.SendValidationCodeOperation;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations.VerifyTwoFactorCodeOperation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v2/auth/2fa")
@Tag(
    name = "Two-factor authentication (TOTP)",
    description =
        "Manage TOTP for the **authenticated** user. All routes require **Bearer** access token.")
@SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
public class TwoFactorConfigController {
  private final TwoFaConfigUseCases authUseCases;

  @Autowired
  public TwoFactorConfigController(TwoFaConfigUseCases authUseCases) {
    this.authUseCases = authUseCases;
  }

  @PostMapping("/enable")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  @EnableTwoFactorAuthOperation
  public ResponseWrapper<TwoFactorQRResult> enableTwoFactorAuth(
      @RequestAttribute("userId") String userId) {
    var command = EnableTwoFactorCommand.of(userId);
    TwoFactorQRResult result = authUseCases.enableTwoFactorAuth(command);
    return ResponseWrapper.success(result, "Scan the QR code with your authenticator app");
  }

  @PostMapping("/confirm")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  @ConfirmTwoFactorSetupOperation
  public ResponseWrapper<Void> confirmTwoFactorSetup(
      @RequestAttribute("userId") String userId,
      @RequestBody @Valid @NotNull ConfirmTwoFactorSetupRequest request) {
    authUseCases.confirmTwoFactorSetup(
        ConfirmTwoFactorSetupCommand.of(userId, request.code()));
    return ResponseWrapper.success(null, "Two-factor authentication enabled");
  }

  @PostMapping("/disable")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  @DisableTwoFactorAuthOperation
  public ResponseWrapper<Void> disableTwoFactorAuth(@RequestAttribute("userId") String userId) {
    var command = DisableTwoFactorCommand.of(userId);
    authUseCases.disableTwoFactorAuth(command);
    return ResponseWrapper.success(null, "2FA disabled");
  }

  @PostMapping("/send-code")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  @SendValidationCodeOperation
  public ResponseWrapper<Void> sendValidationCode(@RequestAttribute("userId") String userId) {
    var command = SendValidationCodeCommand.of(userId);
    authUseCases.sendValidationCode(command);
    return ResponseWrapper.success(null, "Validation code sent");
  }

  @PostMapping("/verify-code")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  @VerifyTwoFactorCodeOperation
  public ResponseWrapper<Void> verifyTwoFactorCode(
      @RequestAttribute("userId") String userId,
      @RequestBody @Valid @NotNull ConfirmTwoFactorSetupRequest request) {
    authUseCases.verifyTwoFactorCode(
        VerifyTwoFactorCommand.of(userId, request.code()));
    return ResponseWrapper.success(null, "Code verified");
  }
}
