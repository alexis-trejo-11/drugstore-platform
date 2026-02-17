package microservice.auth_service.app.auth.core.application.command;

public record ResetPasswordToken(
		String code,
		String newPassword
) {
	public ResetPasswordToken {
		if (code == null || code.isBlank()) {
			throw new IllegalArgumentException("Code cannot be null or blank");
		}
		if (newPassword == null || newPassword.isBlank()) {
			throw new IllegalArgumentException("New password cannot be null or blank");
		}
	}
}
