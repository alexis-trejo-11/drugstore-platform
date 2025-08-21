package microservice.user_service.auth.core.application.dto;

import microservice.user_service.users.core.domain.models.enums.UserRole;

public record SignupDTO(
    String email,
    String phoneNumber,
    String password,
    UserRole userRole
) {
    public SignupDTO {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        if (userRole == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }
    }
}