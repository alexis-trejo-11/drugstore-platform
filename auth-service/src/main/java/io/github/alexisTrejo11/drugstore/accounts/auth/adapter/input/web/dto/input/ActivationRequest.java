package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Email / account activation token from outbound mail")
public record ActivationRequest(
    @Schema(
            description = "Opaque activation code from the verification link",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String activationCode) {}
