// 4. TwoFactorQRResponse.java (NUEVO)
package microservice.auth.app.auth.adapter.input.web.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.auth.app.auth.core.application.result.TwoFactorQRResult;

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
				.qrCodeUrl(result.getQrCodeUrl())
				.secret(result.getSecret())
				.manualEntryKey(result.getManualEntryKey())
				.build();
	}
}