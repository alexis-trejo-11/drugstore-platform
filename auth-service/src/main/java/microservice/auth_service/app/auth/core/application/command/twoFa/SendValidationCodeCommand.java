package microservice.auth_service.app.auth.core.application.command.twoFa;

import microservice.auth_service.app.auth.core.domain.valueobjects.UserId;

public record SendValidationCodeCommand(UserId userId) {
	public static SendValidationCodeCommand of(String userId) {
		return new SendValidationCodeCommand(new UserId(userId));
	}
}
