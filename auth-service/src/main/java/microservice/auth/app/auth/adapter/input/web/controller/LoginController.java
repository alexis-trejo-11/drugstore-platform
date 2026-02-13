package microservice.auth.app.auth.adapter.input.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import libs_kernel.response.ResponseWrapper;
import microservice.auth.app.auth.adapter.input.web.dto.input.LoginRequest;
import microservice.auth.app.auth.adapter.input.web.dto.input.OAuth2LoginRequest;
import microservice.auth.app.auth.adapter.input.web.dto.input.TwoFactorLoginRequest;
import microservice.auth.app.auth.adapter.input.web.dto.output.SessionResponse;
import microservice.auth.app.auth.core.application.command.LoginCommand;
import microservice.auth.app.auth.core.application.command.OAuth2LoginCommand;
import microservice.auth.app.auth.core.application.command.RefreshAccessTokenCommand;
import microservice.auth.app.auth.core.application.command.TwoFactorLoginCommand;
import microservice.auth.app.auth.core.application.result.SessionResult;
import microservice.auth.app.auth.core.ports.input.AuthUseCases;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/auth")
public class LoginController {
	private final AuthUseCases authUseCases;

	@Autowired
	public LoginController(AuthUseCases authUseCases) {
		this.authUseCases = authUseCases;
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseWrapper<SessionResponse>> login(
			@RequestBody @Valid @NotNull LoginRequest request) {
		LoginCommand command = request.toCommand();
		SessionResult result = authUseCases.login(command);
		SessionResponse response = SessionResponse.fromResult(result);

		return ResponseEntity.ok(
				ResponseWrapper.success(response, "Login successfully processed")
		);
	}

	@PostMapping("/login/oauth2")
	public ResponseEntity<ResponseWrapper<SessionResponse>> oauth2Login(
			@RequestBody @Valid @NotNull OAuth2LoginRequest request) {
		OAuth2LoginCommand command = request.toCommand();
		SessionResult result = authUseCases.oauth2Login(command);
		SessionResponse response = SessionResponse.fromResult(result);

		return ResponseEntity.ok(
				ResponseWrapper.success(response, "OAuth2 login successfully processed")
		);
	}

	@PostMapping("/login/2fa")
	public ResponseEntity<ResponseWrapper<SessionResponse>> twoFactorLogin(
			@RequestBody @Valid @NotNull TwoFactorLoginRequest request) {
		TwoFactorLoginCommand command = request.toCommand();
		SessionResult result = authUseCases.twoFactorLogin(command);
		SessionResponse response = SessionResponse.fromResult(result);

		return ResponseEntity.ok(
				ResponseWrapper.success(response, "2FA login successfully processed")
		);
	}

	@PatchMapping("/refresh-session/{refreshToken}")
	public ResponseEntity<ResponseWrapper<SessionResponse>> refreshSession(
			@PathVariable @Valid @NotBlank String refreshToken) {
		RefreshAccessTokenCommand command = new RefreshAccessTokenCommand(refreshToken);
		SessionResult result = authUseCases.refreshAccessToken(command);
		SessionResponse response = SessionResponse.fromResult(result);

		return ResponseEntity.ok(
				ResponseWrapper.success(response, "Access token refreshed successfully")
		);
	}
}