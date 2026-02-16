package microservice.auth.app.auth.adapter.input.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.response.ResponseWrapper;
import microservice.auth.app.auth.core.application.command.twoFa.DisableTwoFactorCommand;
import microservice.auth.app.auth.core.application.command.twoFa.EnableTwoFactorCommand;
import microservice.auth.app.auth.core.application.command.twoFa.SendValidationCodeCommand;
import microservice.auth.app.auth.core.ports.input.TwoFaConfigUseCases;

@RestController
@RequestMapping("/api/v1/auth/2fa")
public class TwoFactorConfigController {
	private final TwoFaConfigUseCases authUseCases;

	@Autowired
	public TwoFactorConfigController(TwoFaConfigUseCases authUseCases) {
		this.authUseCases = authUseCases;
	}

	@PostMapping("{userId}/enable")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseWrapper<String> enableTwoFactorAuth(@PathVariable String userId) {
		var command = EnableTwoFactorCommand.of(userId);
		authUseCases.enableTwoFactorAuth(command);
		return ResponseWrapper.success("2FA enabled for user: " + userId);
	}

	@PostMapping("{userId}/disable")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseWrapper<String> disableTwoFactorAuth(@PathVariable String userId) {
		var command = DisableTwoFactorCommand.of(userId);
		authUseCases.disableTwoFactorAuth(command);
		return ResponseWrapper.success("2FA disabled for user: " + userId);
	}

	@PostMapping("{userId}/send-code")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseWrapper<String> sendValidationCode(@PathVariable String userId) {
		var command = SendValidationCodeCommand.of(userId);
		authUseCases.sendValidationCode(command);
		return ResponseWrapper.success("Validation code sent to user: " + userId);
	}
}
