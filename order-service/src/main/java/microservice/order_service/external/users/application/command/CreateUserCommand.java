package microservice.order_service.external.users.application.command;


import lombok.Builder;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

import java.time.LocalDateTime;

@Builder
public record CreateUserCommand(
    String name,
    String id,
    String email,
    String phoneNumber,
    String status,
    String role
) {

    public User toDomain() {
        return User.builder()
                .phoneNumber(phoneNumber())
                .email(email())
                .name(name())
                .role(role())
                .status(status())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}


