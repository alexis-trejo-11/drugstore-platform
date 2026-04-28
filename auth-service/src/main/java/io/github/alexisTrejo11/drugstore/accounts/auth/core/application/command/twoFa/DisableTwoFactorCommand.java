package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

/**
 * Command for disabling two-factor authentication for a user.
 * Validates that userId is provided.
 */
public record DisableTwoFactorCommand(UserId userId) {
  public DisableTwoFactorCommand {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
  }

  public static DisableTwoFactorCommand of(String userId) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID string cannot be null or blank");
    }
    return new DisableTwoFactorCommand(new UserId(userId));
  }
}
