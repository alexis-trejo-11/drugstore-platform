package microservice.order_service.external.users.infrastructure.api.dto;

import lombok.Builder;

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
}
