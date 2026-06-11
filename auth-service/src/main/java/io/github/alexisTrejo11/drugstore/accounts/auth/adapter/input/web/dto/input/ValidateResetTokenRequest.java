package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ValidateResetTokenCommand;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Check that a password-reset token is still valid before showing change form")
public class ValidateResetTokenRequest {
    @NotBlank(message = "Reset token is required")
    @Schema(description = "Token from email deep-link", requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;

    public ValidateResetTokenCommand toCommand() {
        return new ValidateResetTokenCommand(this.token);
    }
}