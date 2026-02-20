package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

public record EnableTwoFactorCommand(UserId userId) {
	public static final EnableTwoFactorCommand of(String userId) {
		return new EnableTwoFactorCommand(new UserId(userId));
	}
}
