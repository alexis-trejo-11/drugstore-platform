package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;

public record SessionPayload(
		Token accessToken,
		Token refreshToken,
		String userId,
		String tokenType
) {

	public static SessionPayload bearer(String userId, Token accessToken, Token refreshToken) {
		return new SessionPayload(accessToken, refreshToken, userId, "Bearer");
	}
}
