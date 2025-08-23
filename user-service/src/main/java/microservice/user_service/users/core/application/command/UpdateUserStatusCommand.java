package microservice.user_service.users.core.application.command;

import microservice.user_service.users.core.application.dto.UserStatusActions;

import java.util.UUID;

public record UpdateUserStatusCommand(
        UUID userId,
        UserStatusActions actions,
        String activationToken) implements Command {
    public static UpdateUserStatusCommand ban(UUID userId) {
        return new UpdateUserStatusCommand(userId, UserStatusActions.BAN, null);
    }

    public static UpdateUserStatusCommand unban(UUID userId) {
        return new UpdateUserStatusCommand(userId, UserStatusActions.UNBAN, null);
    }

    public static UpdateUserStatusCommand activate(UUID userId, String activationToken) {
        return new UpdateUserStatusCommand(userId, UserStatusActions.ACTIVATE, activationToken);
    }
}
