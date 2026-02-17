package microservice.auth_service.app.auth.adapter.input.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.response.ResponseWrapper;
import microservice.auth_service.app.auth.core.application.command.LogoutAllCommand;
import microservice.auth_service.app.auth.core.application.command.LogoutCommand;
import microservice.auth_service.app.auth.core.ports.input.LogoutUseCases;

@RestController
@RequestMapping("/api/v2/auth")
public class LogoutController {
	private final LogoutUseCases logoutUseCases;

	@Autowired
	public LogoutController(LogoutUseCases logoutUseCases) {
		this.logoutUseCases = logoutUseCases;
	}

	@PostMapping("/logout/{refreshToken}")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseEntity<ResponseWrapper<Void>> logout(
			@PathVariable @Valid @NotBlank String refreshToken) {
		LogoutCommand command = new LogoutCommand(refreshToken);
		logoutUseCases.logout(command);

		return ResponseEntity.ok(
				ResponseWrapper.success(null, "Logout successfully processed"));
	}

	@PostMapping("/logout-all")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseEntity<ResponseWrapper<Void>> logoutAll(
			@RequestAttribute("userId") String userId) {
		LogoutAllCommand command = new LogoutAllCommand(userId);
		logoutUseCases.logoutAll(command);

		return ResponseEntity.ok(
				ResponseWrapper.success(null, "All sessions logged out successfully"));
	}
}