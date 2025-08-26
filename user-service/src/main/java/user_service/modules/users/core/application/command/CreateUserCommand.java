package user_service.modules.users.core.application.command;

import lombok.Builder;
import user_service.modules.users.core.domain.models.enums.UserRole;

@Builder
public record CreateUserCommand(
        String email,
        String phoneNumber,
        UserRole role,
        String password) implements Command {
}
