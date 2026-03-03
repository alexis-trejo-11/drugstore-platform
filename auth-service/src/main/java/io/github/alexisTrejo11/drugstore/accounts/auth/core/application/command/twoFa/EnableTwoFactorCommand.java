package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

/**
 * Command for enabling two-factor authentication for a user.
 * Validates that userId is provided.
 */
public record EnableTwoFactorCommand(UserId userId) {
  public EnableTwoFactorCommand {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
  }

  public static EnableTwoFactorCommand of(String userId) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID string cannot be null or blank");
    }
    return new EnableTwoFactorCommand(new UserId(userId));
  }
}
