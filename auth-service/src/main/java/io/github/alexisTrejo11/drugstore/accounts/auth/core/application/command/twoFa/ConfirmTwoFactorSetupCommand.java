package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

/** Completes TOTP enrollment after user scans QR and enters a valid code. */
public record ConfirmTwoFactorSetupCommand(UserId userId, String verificationCode) {

  public ConfirmTwoFactorSetupCommand {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
    if (verificationCode == null || verificationCode.isBlank()) {
      throw new IllegalArgumentException("Verification code cannot be blank");
    }
  }

  public static ConfirmTwoFactorSetupCommand of(String userId, String verificationCode) {
    return new ConfirmTwoFactorSetupCommand(new UserId(userId), verificationCode.trim());
  }
}
