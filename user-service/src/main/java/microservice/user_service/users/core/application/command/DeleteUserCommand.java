package microservice.user_service.users.core.application.command;

import java.util.UUID;

public record DeleteUserCommand(UUID userId) implements Command {}
