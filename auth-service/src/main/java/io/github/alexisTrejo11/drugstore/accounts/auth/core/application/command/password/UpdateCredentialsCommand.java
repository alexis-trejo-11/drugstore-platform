package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

/**
 * Update email and/or phone in the user service. Blank values keep the current value.
 */
public record UpdateCredentialsCommand(UserId userId, String newEmail, String newPhone) {

  public static UpdateCredentialsCommand of(String userId, String newEmail, String newPhone) {
    return new UpdateCredentialsCommand(new UserId(userId), newEmail, newPhone);
  }
}
