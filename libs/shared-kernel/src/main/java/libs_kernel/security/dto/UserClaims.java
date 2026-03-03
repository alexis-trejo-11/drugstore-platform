package libs_kernel.security.dto;

import lombok.Builder;

@Builder
public record UserClaims(
		String userId,
		String role,
		String email,
		String name,
		String phoneNumber
) {
}
