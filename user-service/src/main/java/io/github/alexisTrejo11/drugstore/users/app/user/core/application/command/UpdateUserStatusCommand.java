package io.github.alexisTrejo11.drugstore.users.app.user.core.application.command;

import io.github.alexisTrejo11.drugstore.users.app.user.core.application.result.UserStatusActions;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.UserId;

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
