package io.github.alexisTrejo11.drugstore.users.user.core.application.command;

import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;

public record DeleteUserCommand(UserId userId) implements Command {
  public static DeleteUserCommand of(String userId) {
    return new DeleteUserCommand(new UserId(userId));
  }

  public DeleteUserCommand {
    if (userId == null) {
      throw new IllegalArgumentException("UserId cannot be null");
    }
  }
}
