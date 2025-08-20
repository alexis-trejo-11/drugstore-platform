package microservice.user_service.users.core.application.command;

import lombok.Builder;
import microservice.user_service.users.core.domain.models.enums.UserRole;

import java.util.Date;

@Builder
public record CreateUserCommand(
        String name,
        String email,
        String phoneNumber,
        Date birthDate,
        String firstName,
        String lastName,
        UserRole role,
        String password
) implements Command {}
