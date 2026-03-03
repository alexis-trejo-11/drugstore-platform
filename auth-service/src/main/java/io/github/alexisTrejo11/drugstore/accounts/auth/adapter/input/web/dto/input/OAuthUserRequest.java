package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

public record OAuthUserRequest(
		String provider,
		String email,
		String name,
		String pictureUrl
) {
}
