package user_service.modules.users.core.application.command;

import lombok.Builder;
import user_service.modules.users.core.domain.models.enums.UserRole;
import user_service.modules.users.core.domain.models.valueobjects.Email;
import user_service.modules.users.core.domain.models.valueobjects.FullName;
import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;

@Builder
public record CreateUserCommand(
    Email email,
    PhoneNumber phoneNumber,
    UserRole role,
    FullName fullName,
    String password) implements Command {
}
