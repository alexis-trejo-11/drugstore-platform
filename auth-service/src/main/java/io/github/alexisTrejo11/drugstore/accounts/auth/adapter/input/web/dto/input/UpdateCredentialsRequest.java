package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Partial update: blank or null fields are ignored (keeps current email/phone).
 */
@Schema(description = "Update email and/or phone for the authenticated user")
public record UpdateCredentialsRequest(
    @Schema(description = "New email; omit to leave unchanged", example = "user@newdomain.com")
        String email,
    @Schema(description = "New phone; omit to leave unchanged", example = "+15559876543")
        String phone) {}
