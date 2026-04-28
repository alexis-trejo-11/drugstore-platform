package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.TwoFactorQRResult;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorQRResponse {
	private String qrCodeUrl;
	private String secret;
	private String manualEntryKey;

	public static TwoFactorQRResponse fromResult(TwoFactorQRResult result) {
		return TwoFactorQRResponse.builder()
				.qrCodeUrl(result.qrCodeUrl())
				.secret(result.secret())
				.manualEntryKey(result.manualEntryKey())
				.build();
	}
}