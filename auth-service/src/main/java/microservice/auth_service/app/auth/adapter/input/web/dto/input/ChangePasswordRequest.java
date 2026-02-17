package microservice.auth_service.app.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import microservice.auth_service.app.auth.core.application.command.password.ChangePasswordCommand;
import microservice.auth_service.app.auth.core.domain.valueobjects.UserId;

public record ChangePasswordRequest(
		@NotBlank(message = "Current password is required")
		String currentPassword,

		@NotBlank(message = "New password is required")
		@Size(min = 8, message = "Password must be at least 8 characters")
		String newPassword,

		@NotBlank(message = "Password confirmation is required")
		String confirmPassword
) {
	public ChangePasswordCommand toCommand(String userId) {
		return new ChangePasswordCommand(
				this.currentPassword(),
				this.newPassword(),
				new UserId(userId)
		);
	}
}