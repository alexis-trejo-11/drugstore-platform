package microservice.order_service.external.users.application;


import lombok.Builder;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

@Builder
public record UpdateUserCommand(
        UserID id,
        String firstName,
        String lastName, String email,
        String phoneNumber,
        String status,
        String role
) {
}


