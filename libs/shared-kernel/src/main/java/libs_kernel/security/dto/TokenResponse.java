package libs_kernel.security.dto;

import lombok.Builder;

@Builder
public record TokenResponse(
		String accessToken,
		String refreshToken,
		String tokenType,
		long expiresIn,
		String userId,
		String role
) {
}
