package user_service.modules.users.core.domain.models.entities;

import java.time.LocalDateTime;

import lombok.Builder;
import user_service.modules.users.core.domain.models.enums.UserRole;
import user_service.modules.users.core.domain.models.enums.UserStatus;
import user_service.modules.users.core.domain.models.valueobjects.Email;
import user_service.modules.users.core.domain.models.valueobjects.FullName;
import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

@Builder
public record ReconstructUserParams(
    UserId id,
    Email email,
    FullName fullName,
    PhoneNumber phoneNumber,
    String hashedPassword,
    UserRole role,
    Boolean twoFactorEnabled,
    String twoFactorId,
    UserStatus status,
    LocalDateTime lastLoginAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {
}