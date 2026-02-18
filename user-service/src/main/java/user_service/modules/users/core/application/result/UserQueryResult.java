package user_service.modules.users.core.application.result;

import java.time.LocalDateTime;

import lombok.Builder;
import user_service.modules.users.core.domain.models.enums.UserRole;
import user_service.modules.users.core.domain.models.enums.UserStatus;
import user_service.modules.users.core.domain.models.valueobjects.Email;
import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

@Builder
public record UserQueryResult(
    UserId id,
    Email email,
    PhoneNumber phoneNumber,
    UserStatus status,
    UserRole role,
    LocalDateTime createdAt,
    LocalDateTime lastLoginAt,
    LocalDateTime updatedAt) {
}
