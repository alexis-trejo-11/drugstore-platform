package microservice.auth.app.auth.adapter.input.web.dto.input;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import microservice.auth.app.auth.core.application.command.LoginCommand;


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
