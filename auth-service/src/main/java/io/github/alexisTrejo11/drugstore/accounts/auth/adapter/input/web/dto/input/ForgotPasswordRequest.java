package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ForgotPasswordCommand;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    public ForgotPasswordCommand toCommand(String ipAddress) {
        return new ForgotPasswordCommand(this.email, ipAddress);
    }
}