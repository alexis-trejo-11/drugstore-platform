package microservice.auth_service.app.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.auth_service.app.auth.core.application.command.password.ResetPasswordCommand;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
	@NotBlank(message = "Reset token is required")
	private String token;

	@NotBlank(message = "New password is required")
	@Size(min = 8, message = "Password must be at least 8 characters")
	private String newPassword;

	@NotBlank(message = "Password confirmation is required")
	private String confirmPassword;

	public ResetPasswordCommand toCommand() {
		return new ResetPasswordCommand(token, newPassword);
	}
}