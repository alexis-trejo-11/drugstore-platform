package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ResetPasswordCommand;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Finalize password reset using token from email")
public class ResetPasswordRequest {
  @NotBlank(message = "Reset token is required")
  @Schema(description = "Single-use reset token", requiredMode = Schema.RequiredMode.REQUIRED)
  private String token;

  @NotBlank(message = "New password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  @Schema(description = "New password", requiredMode = Schema.RequiredMode.REQUIRED)
  private String newPassword;

  @NotBlank(message = "Password confirmation is required")
  @Schema(description = "Must match `newPassword`", requiredMode = Schema.RequiredMode.REQUIRED)
  private String confirmPassword;

	public ResetPasswordCommand toCommand() {
		return new ResetPasswordCommand(token, newPassword);
	}
}