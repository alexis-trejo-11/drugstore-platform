package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "TOTP code from authenticator (confirm setup or verify session)")
public record ConfirmTwoFactorSetupRequest(
    @Schema(
            description = "6-digit one-time code",
            example = "123456",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String code) {}
