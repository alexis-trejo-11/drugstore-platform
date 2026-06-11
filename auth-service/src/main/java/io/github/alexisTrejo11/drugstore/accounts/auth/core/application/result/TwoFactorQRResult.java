package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authenticator setup payload after enabling TOTP")
public record TwoFactorQRResult(
    @Schema(description = "User subject id") String userId,
    @Schema(description = "otpauth:// URL for QR rendering in authenticator apps")
        String qrCodeUrl,
    @Schema(description = "Raw TOTP secret (handle carefully)") String secret,
    @Schema(description = "Human-readable secret for manual entry") String manualEntryKey) {}