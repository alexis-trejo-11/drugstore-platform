package microservice.auth_service.app.auth.core.application.command.twoFa;

import microservice.auth_service.app.auth.core.domain.valueobjects.UserId;

public record EnableTwoFactorCommand(UserId userId) {
	public static final EnableTwoFactorCommand of(String userId) {
		return new EnableTwoFactorCommand(new UserId(userId));
	}
}
