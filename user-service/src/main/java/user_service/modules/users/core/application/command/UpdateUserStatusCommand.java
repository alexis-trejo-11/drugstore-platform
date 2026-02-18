package user_service.modules.users.core.application.command;

import user_service.modules.users.core.application.result.UserStatusActions;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

public record UpdateUserStatusCommand(
        UserId userId,
        UserStatusActions actions,
        String activationToken) implements Command {
    public static UpdateUserStatusCommand ban(String userId) {
        return new UpdateUserStatusCommand(new UserId(userId), UserStatusActions.BAN, null);
    }

    public static UpdateUserStatusCommand unban(String userId) {
        return new UpdateUserStatusCommand(new UserId(userId), UserStatusActions.UNBAN, null);
    }

    public static UpdateUserStatusCommand activate(String userId, String activationToken) {
        return new UpdateUserStatusCommand(new UserId(userId), UserStatusActions.ACTIVATE, activationToken);
    }
}
