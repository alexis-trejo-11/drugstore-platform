package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command;

/**
 * Command for logging out all user sessions across all devices.
 * Validates that userId is provided.
 */
public record LogoutAllCommand(String userId) {
  public LogoutAllCommand {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID cannot be null or blank");
    }
  }
}
