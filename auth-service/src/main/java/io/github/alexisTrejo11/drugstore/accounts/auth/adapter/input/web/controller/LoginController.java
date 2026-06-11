package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input.LoginRequest;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input.RefreshSessionRequest;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input.TwoFactorLoginRequest;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.output.SessionResponse;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.RefreshAccessTokenCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.LoginCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.TwoFactorLoginCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SessionPayload;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.AuthUseCases;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations.LoginOperation;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations.RefreshSessionOperation;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations.TwoFactorLoginOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.response.ResponseWrapper;

@RestController
@RequestMapping("/api/v2/auth")
@Tag(
    name = "Session & login",
    description =
        "Password login, second-factor completion, and refresh-token rotation. "
            + "Rate limit: **SENSITIVE** on login/2FA, **STANDARD** on refresh.")
public class LoginController {
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(LoginController.class);
  private final AuthUseCases authUseCases;

  @Autowired
  public LoginController(AuthUseCases authUseCases) {
    this.authUseCases = authUseCases;
  }

  @PostMapping("/login")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  @LoginOperation
  public ResponseWrapper<SessionResponse> login(
      @RequestBody @Valid @NotNull LoginRequest request) {
    log.info("Login request received for identifier: {}", maskIdentifier(request.emailOrPhoneNumber()));

    LoginCommand command = request.toCommand();
    SessionPayload result = authUseCases.login(command);
    SessionResponse response = SessionResponse.fromResult(result);

    if (result.requiresTwoFactor()) {
      return ResponseWrapper.success(
          response, "Two-factor code sent. Complete login at /api/v2/auth/login/2fa");
    }
    log.info("Login successful for user: {}", result.userId());
    return ResponseWrapper.success(response, "Login successfully processed");
  }

  @PostMapping("/login/2fa")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  @TwoFactorLoginOperation
  public ResponseWrapper<SessionResponse> twoFactorLogin(
      @RequestBody @Valid @NotNull TwoFactorLoginRequest request) {
    log.info("2FA login request received");

    TwoFactorLoginCommand command = request.toCommand();
    SessionPayload result = authUseCases.twoFactorLogin(command);

    SessionResponse response = SessionResponse.fromResult(result);
    log.info("2FA login successful for user: {}", result.userId());
    return ResponseWrapper.success(response, "2FA login successfully processed");
  }

  @PostMapping("/session/refresh")
  @RateLimit(profile = RateLimitProfile.STANDARD)
  @RefreshSessionOperation
  public ResponseWrapper<SessionResponse> refreshSession(
      @RequestBody @Valid @NotNull RefreshSessionRequest request) {
    log.debug("Token refresh request received");

    RefreshAccessTokenCommand command = new RefreshAccessTokenCommand(request.refreshToken());
    SessionPayload result = authUseCases.refreshAccessToken(command);
    SessionResponse response = SessionResponse.fromResult(result);

    log.debug("Access token refreshed successfully");
    return ResponseWrapper.success(response, "Access token refreshed successfully");
  }

  private String maskIdentifier(String identifier) {
    if (identifier == null || identifier.length() < 3) {
      return "***";
    }
    return identifier.substring(0, 3) + "***";
  }
}
