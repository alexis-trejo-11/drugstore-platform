package microservice.users.core.application.command;

import microservice.users.core.domain.models.enums.UserRole;

public record CreateUserCommand(
        String name,
        String email,
        String phoneNumber,
        String firstName,
        String lastName,
        UserRole role,
        String password
) implements Command {}
