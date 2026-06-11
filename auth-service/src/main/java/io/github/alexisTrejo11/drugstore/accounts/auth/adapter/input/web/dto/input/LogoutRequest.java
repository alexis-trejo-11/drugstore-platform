package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Invalidate a single refresh session")
public record LogoutRequest(
    @Schema(
            description = "Refresh token to revoke",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String refreshToken) {}
