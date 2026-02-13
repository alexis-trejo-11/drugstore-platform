package microservice.auth.app.auth.adapter.input.web.controller;


import libs_kernel.response.ResponseWrapper;
import microservice.auth.app.auth.core.domain.valueobjects.UserId;
import microservice.auth.app.auth.core.ports.input.TwoFaConfigUseCases;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/2fa")
public class TwoFactorConfigController {
	private final TwoFaConfigUseCases authUseCases;

	@PostMapping("{userId}/enable")
	public ResponseWrapper<String> enableTwoFactorAuth(@PathVariable String userId) {
		var userIdOBJ = new UserId(userId);
		authUseCases.enableTwoFactorAuth(userIdOBJ);
		return ResponseWrapper.success("2FA enabled for user: " + userId);
	}

	@PostMapping("{userId}/disable")
	public ResponseWrapper<String> disableTwoFactorAuth(@PathVariable String userId) {
		var userIdOBJ = new UserId(userId);
		authUseCases.disableTwoFactorAuth(userIdOBJ);
		return ResponseWrapper.success("2FA disabled for user: " + userId);
	}

	@PostMapping("{userId}/send-code")
	public ResponseWrapper<String> sendValidationCode(@PathVariable String userId) {
		var userIdOBJ = new UserId(userId);
		authUseCases.sendValidationCode(userIdOBJ);
		return ResponseWrapper.success("Validation code sent to user: " + userId);
	}
}
