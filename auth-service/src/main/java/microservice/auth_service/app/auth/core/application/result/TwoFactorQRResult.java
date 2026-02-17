package microservice.auth_service.app.auth.core.application.result;

public record TwoFactorQRResult(
		String qrCodeUrl,
		String secret,
		String manualEntryKey
) {
}