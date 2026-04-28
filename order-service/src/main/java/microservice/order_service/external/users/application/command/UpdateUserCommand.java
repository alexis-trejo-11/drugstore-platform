package microservice.order_service.external.users.application.command;


import lombok.Builder;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

@Builder
public record UpdateUserCommand(
        UserID id,
        String name,
        String email,
        String phoneNumber,
        String status,
        String role
) {
}


