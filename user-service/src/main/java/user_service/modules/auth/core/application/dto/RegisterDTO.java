package user_service.modules.auth.core.application.dto;

import java.time.LocalDateTime;

import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.domain.models.enums.UserRole;

public record RegisterDTO(
        String email,
        String phoneNumber,
        String password,
        UserRole userRole,
        CreateProfileDTO profile) {
    public RegisterDTO {
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

    public User toUser() {
        return User.builder()
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .hashedPassword(this.password)
                .role(this.userRole)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}