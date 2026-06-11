package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ForgotPasswordCommand;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request a password reset link / token for the account email")
public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(
        description = "Account email to send reset instructions to",
        example = "user@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    public ForgotPasswordCommand toCommand(String ipAddress) {
        return new ForgotPasswordCommand(this.email, ipAddress);
    }
}