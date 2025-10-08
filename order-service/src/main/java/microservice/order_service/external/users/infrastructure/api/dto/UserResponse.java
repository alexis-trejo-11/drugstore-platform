package microservice.order_service.external.users.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import microservice.order_service.external.users.domain.entity.User;

@Builder
@Schema(description = "User response DTO containing user details")
public record UserResponse(
        @Schema(
                description = "Unique identifier of the user",
                example = "550e8400-e29b-41d4-a716-446655440000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String id,

        @Schema(
                description = "Full name of the user",
                example = "Carol White",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 1,
                maxLength = 255
        )
        String name,

        @Schema(
                description = "Email address of the user",
                example = "carol.white@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "email"
        )
        String email,

        @Schema(
                description = "Phone number of the user",
                example = "+1234567890",
                requiredMode = Schema.RequiredMode.REQUIRED,
                pattern = "^\\+?[\\d\\s-()]+$"
        )
        String phoneNumber,

        @Schema(
                description = "Role of the user in the system",
                example = "CUSTOMER",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"ADMIN", "CUSTOMER", "MANAGER", "EMPLOYEE"}
        )
        String role,

        @Schema(
                description = "Current status of the user account",
                example = "ACTIVE",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED", "PENDING"}
        )
        String status,

        @Schema(
                description = "Timestamp when the user was created",
                example = "2024-01-15T10:30:00.000Z",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "date-time"
        )
        String createdAt,

        @Schema(
                description = "Timestamp when the user was last updated",
                example = "2024-01-20T14:45:30.000Z",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "date-time"
        )
        String updatedAt
) {

    public static UserResponse from(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId() != null ? user.getId().value() : null)
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole() != null ? user.getRole() : null)
                .status(user.getStatus() != null ? user.getStatus() : null)
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                .build();
    }
}