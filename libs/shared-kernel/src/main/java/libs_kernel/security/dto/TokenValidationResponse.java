package libs_kernel.security.jwt;

import lombok.Builder;

@Builder
public record TokenValidationResponse(
		boolean isValid,
		String userId,
		String role,
		String message) {
}