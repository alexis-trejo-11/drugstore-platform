package microservice.user_service.users.core.application.command;

import lombok.Builder;
import microservice.user_service.users.core.domain.models.enums.UserRole;

@Builder
public record CreateUserCommand(
        String email,
        String phoneNumber,
        UserRole role,
        String password) implements Command {
}
