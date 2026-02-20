package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

public record VerifyTwoFactorCommand(UserId userId, String code) {
}
