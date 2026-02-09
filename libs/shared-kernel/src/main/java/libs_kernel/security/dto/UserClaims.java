package libs_kernel.security.jwt;

public record UserClaims(
		String userId,
		String role,
		String email,
		String name,
		String phoneNumber
) {
}
