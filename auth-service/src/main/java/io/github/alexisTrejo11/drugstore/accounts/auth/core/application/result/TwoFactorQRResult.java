package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result;

public record TwoFactorQRResult(
		String userId,
		String qrCodeUrl,
		String secret,
		String manualEntryKey
) {
}