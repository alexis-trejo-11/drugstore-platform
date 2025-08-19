package microservice.users.core.application.command;

import microservice.users.core.domain.models.enums.UserStatus;

import java.util.UUID;

public record UpdateUserStatusCommand(
        UUID userId,
        UserStatus status
) implements Command {}
