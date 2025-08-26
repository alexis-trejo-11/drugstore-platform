package user_service.modules.users.core.application.dto;

import lombok.Builder;
import user_service.modules.users.core.domain.models.enums.UserRole;
import user_service.modules.users.core.domain.models.enums.UserStatus;

import java.util.UUID;

@Builder
public record UserResponse(
                UUID id,
                String email,
                String phoneNumber,
                UserStatus status,
                UserRole role,
                String joinedAt,
                String lastLoginAt) {
}
