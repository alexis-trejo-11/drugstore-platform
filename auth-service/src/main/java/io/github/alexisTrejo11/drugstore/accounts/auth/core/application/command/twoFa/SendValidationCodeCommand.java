package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

/**
 * Command for sending validation code to user.
 * Validates that userId is provided.
 */
public record SendValidationCodeCommand(UserId userId) {
  public SendValidationCodeCommand {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
  }

  public static SendValidationCodeCommand of(String userId) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID string cannot be null or blank");
    }
    return new SendValidationCodeCommand(new UserId(userId));
  }
}
