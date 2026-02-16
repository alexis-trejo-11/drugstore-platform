package microservice.auth.app.auth.core.application.command.login;

import lombok.Builder;
import microservice.auth.app.auth.core.domain.valueobjects.OAuth2Provider;

@Builder
public record OAuth2LoginCommand(
		String token,
		OAuth2Provider provider,
		String deviceId,
		String deviceName,
		String ipAddress
) {
}
