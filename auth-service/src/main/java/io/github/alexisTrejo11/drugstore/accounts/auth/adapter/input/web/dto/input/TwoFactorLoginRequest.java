package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.TwoFactorLoginCommand;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorLoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;


    @NotBlank(message = "2FA code is required")
    @Size(min = 6, max = 6, message = "2FA code must be 6 digits")
    private String twoFactorCode;

    private String deviceId;
    private String deviceName;
    private String ipAddress;

    public TwoFactorLoginCommand toCommand() {
        return TwoFactorLoginCommand.builder()
                .email(this.email)
                .code(this.twoFactorCode)
                .deviceId(this.deviceId)
                .ipAddress(this.ipAddress)
                .build();
    }
}