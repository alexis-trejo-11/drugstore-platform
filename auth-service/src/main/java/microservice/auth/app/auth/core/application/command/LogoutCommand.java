package microservice.auth.app.auth.core.application.command;

public record LogoutCommand(
		String refreshToken
) {
}