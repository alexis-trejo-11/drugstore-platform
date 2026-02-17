package microservice.auth_service.app.auth.core.application.command;

public record RefreshAccessTokenCommand(String refreshToken) {
	public RefreshAccessTokenCommand{
		if (refreshToken == null || refreshToken.isBlank()) {
			throw new IllegalArgumentException("Refresh token must not be null or blank");
		}
	}
}
