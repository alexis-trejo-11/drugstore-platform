package microservice.auth.app.auth.core.application.command.password;

public record ResetPasswordCommand(String token, String newPassword) {
}
