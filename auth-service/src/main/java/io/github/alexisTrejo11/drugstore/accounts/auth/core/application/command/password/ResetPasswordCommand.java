package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password;

/**
 * Command for resetting user password with a valid reset token.
 * Validates token and new password strength.
 */
public record ResetPasswordCommand(String token, String newPassword) {
  public ResetPasswordCommand {
    if (token == null || token.isBlank()) {
      throw new IllegalArgumentException("Reset token cannot be null or blank");
    }
    if (newPassword == null || newPassword.isBlank()) {
      throw new IllegalArgumentException("New password cannot be null or blank");
    }
    if (newPassword.length() < 8) {
      throw new IllegalArgumentException("New password must be at least 8 characters long");
    }
  }
}
