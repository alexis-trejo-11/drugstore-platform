package microservice.auth_service.app.auth.core.application.command.password;

public record ResetPasswordCommand(String token, String newPassword) {
}
