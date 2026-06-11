package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ChangePasswordCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authenticated password change (requires valid access token)")
public record ChangePasswordRequest(
    @Schema(description = "Existing password", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Current password is required")
        String currentPassword,
    @Schema(description = "New password (min 8 chars)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String newPassword,
    @Schema(description = "Must match `newPassword`", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Password confirmation is required")
        String confirmPassword) {
  public ChangePasswordCommand toCommand(String userId) {
    return new ChangePasswordCommand(
        this.newPassword(),
        new UserId(userId),
        this.currentPassword());
  }
}