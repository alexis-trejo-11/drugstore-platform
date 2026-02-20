package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.login.OAuth2LoginCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.OAuth2Provider;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2LoginRequest {
    @NotBlank(message = "OAuth2 token is required")
    private String token;

    @NotNull(message = "Provider is required")
    private OAuth2Provider provider;

    private String deviceId;
    private String deviceName;
    private String ipAddress;

    public OAuth2LoginCommand toCommand() {
        return OAuth2LoginCommand.builder()
                .token(this.token)
                .provider(this.provider)
                .deviceId(this.deviceId)
                .deviceName(this.deviceName)
                .ipAddress(this.ipAddress)
                .build();
    }
}