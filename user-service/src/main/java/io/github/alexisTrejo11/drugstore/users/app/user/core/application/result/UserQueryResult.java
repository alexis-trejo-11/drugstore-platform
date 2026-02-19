package io.github.alexisTrejo11.drugstore.users.app.user.core.application.result;

import java.time.LocalDateTime;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.enums.UserRole;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.enums.UserStatus;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.UserId;

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
