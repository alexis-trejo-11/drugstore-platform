package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

public record SendValidationCodeCommand(UserId userId) {
	public static SendValidationCodeCommand of(String userId) {
		return new SendValidationCodeCommand(new UserId(userId));
	}
}
