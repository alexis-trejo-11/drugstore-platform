package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

/**
 * Command for verifying two-factor authentication code.
 * Validates that both userId and code are provided.
 */
public record VerifyTwoFactorCommand(UserId userId, String code) {
  public VerifyTwoFactorCommand {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
    if (code == null || code.isBlank()) {
      throw new IllegalArgumentException("Verification code cannot be null or blank");
    }
    if (!code.matches("\\d+")) {
      throw new IllegalArgumentException("Verification code must contain only digits");
    }
  }
}
