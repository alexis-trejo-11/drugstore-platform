package io.github.alexisTrejo11.drugstore.users.user.core.domain.models.entities;

import java.time.LocalDateTime;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserRole;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserStatus;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.FullName;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;

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