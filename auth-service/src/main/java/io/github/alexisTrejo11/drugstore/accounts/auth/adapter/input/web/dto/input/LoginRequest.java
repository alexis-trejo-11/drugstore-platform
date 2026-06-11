package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.LoginCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Primary login credentials and optional device context")
public record LoginRequest(
    @Schema(
            description = "Email address or E.164 phone number used as login identifier",
            example = "user@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Email or Phone is required")
        String emailOrPhoneNumber,
    @Schema(description = "Plain-text password", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Password is required")
        String password,
    @Schema(description = "Stable device id for session tracking") String deviceId,
    @Schema(description = "Human-readable device label") String deviceName,
    @Schema(description = "Client IP; may be overridden by infrastructure") String ipAddress) {
	public LoginCommand toCommand() {
		return new LoginCommand(
				this.emailOrPhoneNumber,
				this.password,
				this.deviceId,
				this.deviceName,
				this.ipAddress);
	}
}
