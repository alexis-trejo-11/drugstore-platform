package microservice.auth.app.auth.core.application.command.twoFa;

import microservice.auth.app.auth.core.domain.valueobjects.UserId;

public record DisableTwoFactorCommand(UserId userId) {

	public static DisableTwoFactorCommand of(String userId) {
		return new DisableTwoFactorCommand(new UserId(userId));
	}
}
