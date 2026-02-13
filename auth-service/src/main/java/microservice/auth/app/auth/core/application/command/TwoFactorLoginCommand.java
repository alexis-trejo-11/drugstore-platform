package microservice.auth.app.auth.core.application.command;

public record TwoFactorLoginCommand(
		String sessionId,
		String code
) {
	public TwoFactorLoginCommand {
		if (sessionId == null || sessionId.isBlank()) {
			throw new IllegalArgumentException("Session ID cannot be null or blank");
		}
		if (code == null || code.isBlank()) {
			throw new IllegalArgumentException("Code cannot be null or blank");
		}
	}
}
