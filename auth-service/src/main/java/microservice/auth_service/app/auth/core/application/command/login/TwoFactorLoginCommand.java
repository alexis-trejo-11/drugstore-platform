package microservice.auth_service.app.auth.core.application.command.login;

import lombok.Builder;

@Builder
public record TwoFactorLoginCommand(
		String email,
		String password,
		String twoFactorCode,
		String deviceId,
		String deviceName,
		String ipAddress
) {
	public TwoFactorLoginCommand {

	}
}
