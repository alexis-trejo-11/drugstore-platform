package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input.LoginRequest;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input.TwoFactorLoginRequest;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.output.SessionResponse;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.RefreshAccessTokenCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.LoginCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.TwoFactorLoginCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SessionPayload;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.AuthUseCases;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.response.ResponseWrapper;

@RestController
@RequestMapping("/api/v2/auth")
public class LoginController {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoginController.class);
  private final AuthUseCases authUseCases;

  @Autowired
  public LoginController(AuthUseCases authUseCases) {
    this.authUseCases = authUseCases;
  }

  @PostMapping("/login")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  public ResponseWrapper<SessionResponse> login(
      @RequestBody @Valid @NotNull LoginRequest request) {
    log.info("Login request received for identifier: {}", maskIdentifier(request.emailOrPhoneNumber()));

    LoginCommand command = request.toCommand();
    SessionPayload result = authUseCases.login(command);
    SessionResponse response = SessionResponse.fromResult(result);

    log.info("Login successful for user: {}", result.userId());
    return ResponseWrapper.success(response, "Login successfully processed");
  }

  @PostMapping("/login/2fa")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  public ResponseWrapper<SessionResponse> twoFactorLogin(
      @RequestBody @Valid @NotNull TwoFactorLoginRequest request) {
    log.info("2FA login request received");

    TwoFactorLoginCommand command = request.toCommand();
    SessionPayload result = authUseCases.twoFactorLogin(command);

    SessionResponse response = SessionResponse.fromResult(result);
    log.info("2FA login successful for user: {}", result.userId());
    return ResponseWrapper.success(response, "2FA login successfully processed");
  }

  @PatchMapping("/refresh-session/{refreshToken}")
  @RateLimit(profile = RateLimitProfile.STANDARD)
  public ResponseWrapper<SessionResponse> refreshSession(
      @PathVariable @Valid @NotBlank String refreshToken) {
    log.debug("Token refresh request received");

    RefreshAccessTokenCommand command = new RefreshAccessTokenCommand(refreshToken);
    SessionPayload result = authUseCases.refreshAccessToken(command);
    SessionResponse response = SessionResponse.fromResult(result);

    log.debug("Access token refreshed successfully");
    return ResponseWrapper.success(response, "Access token refreshed successfully");
  }

  /**
   * Masks identifier for security logging (shows first 3 chars only)
   */
  private String maskIdentifier(String identifier) {
    if (identifier == null || identifier.length() < 3) {
      return "***";
    }
    return identifier.substring(0, 3) + "***";
  }
}