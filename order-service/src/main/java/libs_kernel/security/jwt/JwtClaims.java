package libs_kernel.security.jwt;

import java.util.Date;
import java.util.Map;

public record JwtClaims(
		String subject,
		 Date issuedAt,
		 Date expiration,
		Map<String, Object> claims
		) {

	public boolean isExpired() {
		return expiration.before(new Date());
	}
}
