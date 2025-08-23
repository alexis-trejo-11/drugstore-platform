package microservice.user_service.users.core.domain.models.entities;

import microservice.user_service.users.core.domain.models.enums.Gender;
import microservice.user_service.users.core.domain.models.enums.UserRole;
import microservice.user_service.users.core.domain.models.enums.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    // Account information
    private UUID id;
    private String email;
    private String phoneNumber;
    private String hashedPassword;
    private UserStatus status;
    private UserRole role;
    private String twoFactorId;

    // Personal information
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;

    // Timestamps
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void validateNotDisabled() {
        if (this.status == UserStatus.INACTIVE || this.status == UserStatus.SUSPENDED
                || this.status == UserStatus.DELETED) {
            throw new IllegalStateException("User account is " + this.status);
        }
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorId != null && !twoFactorId.isEmpty();
    }

    public void enableTwoFactor(String twoFactorId) {
        this.twoFactorId = twoFactorId;
        this.updatedAt = LocalDateTime.now();
    }

    public void disableTwoFactor() {
        this.twoFactorId = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (this.status != UserStatus.PENDING) {
            throw new IllegalStateException("User account is not pending activation.");
        }
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(String firstName, String lastName, LocalDate dateOfBirth, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void ban() {
        if (this.status == UserStatus.SUSPENDED) {
            throw new IllegalStateException("User account is already suspended.");
        }

        this.status = UserStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void unban() {
        if (this.status != UserStatus.SUSPENDED) {
            throw new IllegalStateException("User account is not suspended.");
        }
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.status = UserStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void restore() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public static void ValidatePasswordStrength(String plainPassword) {
        if (plainPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
        // Additional checks can be added as needed
    }

    public void updateAuthFields(String email, String phoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }

}