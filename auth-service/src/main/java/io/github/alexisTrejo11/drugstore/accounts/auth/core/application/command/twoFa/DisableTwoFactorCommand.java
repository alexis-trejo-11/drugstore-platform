package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

public record DisableTwoFactorCommand(UserId userId) {

	public static DisableTwoFactorCommand of(String userId) {
		return new DisableTwoFactorCommand(new UserId(userId));
	}
}
