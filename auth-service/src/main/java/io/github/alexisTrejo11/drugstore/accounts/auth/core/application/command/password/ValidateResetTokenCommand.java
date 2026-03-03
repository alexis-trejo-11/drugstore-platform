package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password;

/**
 * Command for validating a password reset token.
 * Validates that token is provided.
 */
public record ValidateResetTokenCommand(String token) {
  public ValidateResetTokenCommand {
    if (token == null || token.isBlank()) {
      throw new IllegalArgumentException("Reset token cannot be null or blank");
    }
  }
}
