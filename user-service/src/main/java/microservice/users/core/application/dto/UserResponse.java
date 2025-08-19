package microservice.users.core.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import microservice.users.core.domain.models.enums.UserRole;
import microservice.users.core.domain.models.enums.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String phoneNumber,
        String hashedPassword,
        UserStatus status,
        UserRole role,
        String joinedAt,
        String lastLoginAt
) {}
