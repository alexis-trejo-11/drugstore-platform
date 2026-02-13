package microservice.auth.app.auth.adapter.input.web.dto.input;

public record UpdatePasswordRequest(
		String currentPassword,
		String newPassword
) {
}
