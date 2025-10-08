package microservice.order_service.external.users.infrastructure.api.dto;

import lombok.Builder;
import microservice.order_service.external.users.domain.entity.User;

@Builder
public record UserResponse(
        String id,
        String name,
        String email,
        String phoneNumber,
        String role,
        String status,
        String createdAt,
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
