package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

/**
 * Command for changing user password.
 * Validates all required fields and password strength.
 */
public record ChangePasswordCommand(String newPassword, UserId userId, String currentPassword) {
  public ChangePasswordCommand {
    if (currentPassword == null || currentPassword.isBlank()) {
      throw new IllegalArgumentException("Current Password cannot be null or blank");
    }
    if (newPassword == null || newPassword.isBlank()) {
      throw new IllegalArgumentException("New password cannot be null or blank");
    }
    if (newPassword.length() < 8) {
      throw new IllegalArgumentException("New password must be at least 8 characters long");
    }
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
  }
}
