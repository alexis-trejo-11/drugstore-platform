package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.TwoFactorLoginCommand;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Completes login after primary credentials verified and 2FA required")
public class TwoFactorLoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "Account email", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "2FA code is required")
    @Size(min = 6, max = 6, message = "2FA code must be 6 digits")
    @Schema(
            description = "6-digit TOTP from authenticator app",
            example = "123456",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String twoFactorCode;

    @Schema(description = "Stable device id for session tracking") private String deviceId;
    @Schema(description = "Human-readable device label") private String deviceName;
    @Schema(description = "Client IP") private String ipAddress;

    public TwoFactorLoginCommand toCommand() {
        return TwoFactorLoginCommand.builder()
                .email(this.email)
                .code(this.twoFactorCode)
                .deviceId(this.deviceId)
                .ipAddress(this.ipAddress)
                .build();
    }
}