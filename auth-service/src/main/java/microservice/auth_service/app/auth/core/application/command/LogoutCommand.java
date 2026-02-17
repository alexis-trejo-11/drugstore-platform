package microservice.auth_service.app.auth.core.application.command;

public record LogoutCommand(
		String refreshToken
) {
}