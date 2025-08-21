package microservice.user_service.users.core.domain.models.entities;

import microservice.user_service.users.core.domain.models.enums.Gender;
import microservice.user_service.users.core.domain.models.enums.UserRole;
import microservice.user_service.users.core.domain.models.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

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
    private Date dateOfBirth;
    private Gender gender;

    // Timestamps
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void validateNotDisabled() {
        if (this.status == UserStatus.INACTIVE || this.status == UserStatus.SUSPENDED || this.status == UserStatus.DELETED) {
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

    public void updateProfile(String firstName, String lastName, Date dateOfBirth, Gender gender) {
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
        this.status = UserStatus.SUSPENDED;
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

    // Constructors/Builder/Getters
    public User(UUID id, String email, String phoneNumber, String hashedPassword, UserStatus status, UserRole role, String firstName, String lastName, Date dateOfBirth, Gender gender, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.hashedPassword = hashedPassword;
        this.status = status;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private User(Builder builder) {
        this.id = builder.id;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.hashedPassword = builder.hashedPassword;
        this.status = builder.status;
        this.role = builder.role;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.dateOfBirth = builder.dateOfBirth;
        this.gender = builder.gender;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public UserStatus getStatus() {
        return status;
    }

    public UserRole getRole() {
        return role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword; // Should be from not hashed to hashed
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void updateAuthFields(String email, String phoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }


    public static class Builder {
        private UUID id;
        private String email;
        private String phoneNumber;
        private String hashedPassword;
        private UserStatus status;
        private UserRole role;
        private String firstName;
        private String lastName;
        private Date dateOfBirth;
        private Gender gender;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder hashedPassword(String hashedPassword) {
            this.hashedPassword = hashedPassword;
            return this;
        }

        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder dateOfBirth(Date dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public User build() {
            if (this.id == null || this.email == null || this.hashedPassword == null || this.status == null || this.role == null) {
                throw new IllegalStateException("Required fields cannot be null.");
            }
            return new User(this);
        }
    }
}