package libs_kernel.security.dto;

public record UserClaims(
		String userId,
		String role,
		String email,
		String name,
		String phoneNumber
) {
}
