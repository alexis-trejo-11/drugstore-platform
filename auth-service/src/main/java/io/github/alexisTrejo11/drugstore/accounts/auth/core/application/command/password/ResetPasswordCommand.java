package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password;

public record ResetPasswordCommand(String token, String newPassword) {
}
