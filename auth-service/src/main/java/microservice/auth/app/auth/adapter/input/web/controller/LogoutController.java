package microservice.auth.app.auth.adapter.input.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import libs_kernel.response.ResponseWrapper;
import microservice.auth.app.auth.core.application.command.LogoutAllCommand;
import microservice.auth.app.auth.core.application.command.LogoutCommand;
import microservice.auth.app.auth.core.ports.input.LogoutUseCases;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/auth")
public class LogoutController {
	private final LogoutUseCases logoutUseCases;

	@Autowired
	public LogoutController(LogoutUseCases logoutUseCases) {
		this.logoutUseCases = logoutUseCases;
	}

	@PostMapping("/logout/{refreshToken}")
	public ResponseEntity<ResponseWrapper<Void>> logout(
			@PathVariable @Valid @NotBlank String refreshToken) {
		LogoutCommand command = new LogoutCommand(refreshToken);
		logoutUseCases.logout(command);

		return ResponseEntity.ok(
				ResponseWrapper.success(null, "Logout successfully processed")
		);
	}

	@PostMapping("/logout-all")
	public ResponseEntity<ResponseWrapper<Void>> logoutAll(
			@RequestAttribute("userId") String userId) {
		LogoutAllCommand command = new LogoutAllCommand(userId);
		logoutUseCases.logoutAll(command);

		return ResponseEntity.ok(
				ResponseWrapper.success(null, "All sessions logged out successfully")
		);
	}
}