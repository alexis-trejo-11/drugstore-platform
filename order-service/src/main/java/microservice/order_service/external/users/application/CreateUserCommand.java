package microservice.order_service.external.users.application;


import lombok.Builder;

@Builder
public record CreateUserCommand(
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String status,
    String role
) {}


