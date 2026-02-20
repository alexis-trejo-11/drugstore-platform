package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

public record ChangePasswordCommand(String token, String newPassword, UserId userId) {
}
