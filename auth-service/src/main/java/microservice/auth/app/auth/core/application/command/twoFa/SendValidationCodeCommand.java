package microservice.auth.app.auth.core.application.command.twoFa;

import microservice.auth.app.auth.core.domain.valueobjects.UserId;

public record SendValidationCodeCommand(UserId userId) {
	public static SendValidationCodeCommand of(String userId) {
		return new SendValidationCodeCommand(new UserId(userId));
	}
}
