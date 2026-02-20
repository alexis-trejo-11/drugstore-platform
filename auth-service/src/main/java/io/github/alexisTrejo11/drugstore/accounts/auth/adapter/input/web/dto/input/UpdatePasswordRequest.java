package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input;

public record UpdatePasswordRequest(
		String currentPassword,
		String newPassword
) {
}
