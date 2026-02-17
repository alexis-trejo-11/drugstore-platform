package microservice.auth_service.app.auth.adapter.input.web.dto.input;


import jakarta.validation.constraints.NotBlank;
import microservice.auth_service.app.auth.core.application.command.login.LoginCommand;


public record LoginRequest(
		@NotBlank(message = "Email or Phone is required") String emailOrPhoneNumber,
		@NotBlank(message = "Password is required") String password,
		String deviceId,
		String deviceName,
		String ipAddress) {
	public LoginCommand toCommand() {
		return LoginCommand.builder()
				.identifier(this.emailOrPhoneNumber)
				.password(this.password)
				.deviceId(this.deviceId)
				.deviceName(this.deviceName)
				.ipAddress(this.ipAddress)
				.build();
	}
}
