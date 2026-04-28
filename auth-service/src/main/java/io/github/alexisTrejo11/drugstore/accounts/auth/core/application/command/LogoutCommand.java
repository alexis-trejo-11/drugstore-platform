package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command;

/**
 * Command for logging out a user session.
 * Validates that refresh token is provided.
 */
public record LogoutCommand(
    String refreshToken) {
  public LogoutCommand {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new IllegalArgumentException("Refresh token cannot be null or blank");
    }
  }
}