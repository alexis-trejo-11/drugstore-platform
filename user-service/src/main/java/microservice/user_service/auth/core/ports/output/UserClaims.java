package microservice.user_service.auth.core.ports.output;

public record UserClaims(
    String userId,
    String email,
    String role
) {}
