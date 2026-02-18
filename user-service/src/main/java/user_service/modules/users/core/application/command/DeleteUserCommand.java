package user_service.modules.users.core.application.command;

import user_service.modules.users.core.domain.models.valueobjects.UserId;

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
