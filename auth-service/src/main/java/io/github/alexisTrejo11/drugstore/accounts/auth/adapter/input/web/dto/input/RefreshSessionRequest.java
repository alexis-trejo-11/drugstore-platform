package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Refresh-token rotation request")
public record RefreshSessionRequest(
    @Schema(
            description = "Valid refresh token issued by login or previous refresh",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String refreshToken) {}
