package libs_kernel.security;

import libs_kernel.exceptions.UnauthorizedException;
import libs_kernel.security.dto.AuthUserDetails;

public class UserAuthValidator {

	public static void assertUserInContext(AuthUserDetails authUserDetails) {
		if (authUserDetails == null) {
			throw new UnauthorizedException("User data is missing in the security context");
		}
	}

	public static void assertUserInContext(String userId) {
		if (userId == null || userId.isEmpty()) {
			throw new UnauthorizedException("User ID is missing in the security context");
		}
	}
}
