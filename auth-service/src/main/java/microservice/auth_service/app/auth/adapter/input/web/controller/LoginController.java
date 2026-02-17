package microservice.auth_service.app.auth.adapter.input.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.response.ResponseWrapper;
import microservice.auth_service.app.auth.adapter.input.web.dto.input.LoginRequest;
import microservice.auth_service.app.auth.adapter.input.web.dto.input.OAuth2LoginRequest;
import microservice.auth_service.app.auth.adapter.input.web.dto.input.TwoFactorLoginRequest;
import microservice.auth_service.app.auth.adapter.input.web.dto.output.SessionResponse;
import microservice.auth_service.app.auth.core.application.command.RefreshAccessTokenCommand;
import microservice.auth_service.app.auth.core.application.command.login.LoginCommand;
import microservice.auth_service.app.auth.core.application.command.login.OAuth2LoginCommand;
import microservice.auth_service.app.auth.core.application.command.login.TwoFactorLoginCommand;
import microservice.auth_service.app.auth.core.application.result.SessionResult;
import microservice.auth_service.app.auth.core.ports.input.AuthUseCases;

@RestController
@RequestMapping("/api/v2/auth")
public class LoginController {
	private final AuthUseCases authUseCases;

	@Autowired
	public LoginController(AuthUseCases authUseCases) {
		this.authUseCases = authUseCases;
	}

	@PostMapping("/login")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseEntity<ResponseWrapper<SessionResponse>> login(
			@RequestBody @Valid @NotNull LoginRequest request) {
		LoginCommand command = request.toCommand();
		SessionResult result = authUseCases.login(command);
		SessionResponse response = SessionResponse.fromResult(result);

		return ResponseEntity.ok(
				ResponseWrapper.success(response, "Login successfully processed"));
	}

	@PostMapping("/login/oauth2")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseEntity<ResponseWrapper<SessionResponse>> oauth2Login(
			@RequestBody @Valid @NotNull OAuth2LoginRequest request) {
		OAuth2LoginCommand command = request.toCommand();
		SessionResult result = authUseCases.oauth2Login(command);
		SessionResponse response = SessionResponse.fromResult(result);

		return ResponseEntity.ok(
				ResponseWrapper.success(response, "OAuth2 login successfully processed"));
	}

	@PostMapping("/login/2fa")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseEntity<ResponseWrapper<SessionResponse>> twoFactorLogin(
			@RequestBody @Valid @NotNull TwoFactorLoginRequest request) {
		TwoFactorLoginCommand command = request.toCommand();
		SessionResult result = authUseCases.twoFactorLogin(command);

		SessionResponse response = SessionResponse.fromResult(result);
		return ResponseEntity.ok(
				ResponseWrapper.success(response, "2FA login successfully processed"));
	}

	@PatchMapping("/refresh-session/{refreshToken}")
	@RateLimit(profile = RateLimitProfile.STANDARD)
	public ResponseEntity<ResponseWrapper<SessionResponse>> refreshSession(
			@PathVariable @Valid @NotBlank String refreshToken) {
		RefreshAccessTokenCommand command = new RefreshAccessTokenCommand(refreshToken);
		SessionResult result = authUseCases.refreshAccessToken(command);
		SessionResponse response = SessionResponse.fromResult(result);

		return ResponseEntity.ok(
				ResponseWrapper.success(response, "Access token refreshed successfully"));
	}
}