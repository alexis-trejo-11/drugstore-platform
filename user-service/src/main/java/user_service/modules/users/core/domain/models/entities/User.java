package user_service.modules.users.core.domain.models.entities;

import java.time.LocalDateTime;

import user_service.modules.users.core.domain.exceptions.UserDisableError;
import user_service.modules.users.core.domain.models.enums.UserRole;
import user_service.modules.users.core.domain.models.enums.UserStatus;
import user_service.modules.users.core.domain.models.valueobjects.Email;
import user_service.modules.users.core.domain.models.valueobjects.FullName;
import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

public class User {
    private UserId id;
    private Email email;
    private FullName fullName;
    private PhoneNumber phoneNumber;
    private String hashedPassword;
    private UserRole role;
    private Boolean twoFactorEnabled;
    private String twoFactorId;
    private UserStatus status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void validateNotDisabled() {
        if (this.status == UserStatus.INACTIVE || this.status == UserStatus.SUSPENDED
                || this.status == UserStatus.DELETED) {
            throw new UserDisableError("User account is " + this.status);
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

    public void updateAuthFields(Email email, PhoneNumber phoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Creates a new user with validation and business logic applied
     * 
     * @param params Parameters for creating a new user
     * @return A new User instance with PENDING status
     * @throws IllegalArgumentException if any validation fails
     */
    public static User createUser(CreateUserParams params) {
        if (params == null) {
            throw new IllegalArgumentException("CreateUserParams cannot be null");
        }
        if (params.email() == null) {
            throw new IllegalArgumentException("Email is required");
        }
        if (params.fullName() == null) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (params.phoneNumber() == null) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (params.hashedPassword() == null || params.hashedPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Hashed password is required");
        }
        if (params.role() == null) {
            throw new IllegalArgumentException("User role is required");
        }

        User user = new User();
        user.id = UserId.generate();
        user.email = params.email();
        user.fullName = params.fullName();
        user.phoneNumber = params.phoneNumber();
        user.hashedPassword = params.hashedPassword();
        user.role = params.role();
        user.twoFactorEnabled = params.twoFactorEnabled() != null ? params.twoFactorEnabled() : false;
        user.twoFactorId = null;
        user.status = UserStatus.PENDING;
        user.lastLoginAt = null;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();

        return user;
    }

    /**
     * Reconstructs an existing user from persistence data without validation
     * 
     * @param params Parameters containing all user data from persistence
     * @return A User instance with all fields set from the parameters
     * @throws IllegalArgumentException if params is null
     */
    public static User reconstructUser(ReconstructUserParams params) {
        if (params == null) {
            throw new IllegalArgumentException("ReconstructUserParams cannot be null");
        }

        User user = new User();
        user.id = params.id();
        user.email = params.email();
        user.fullName = params.fullName();
        user.phoneNumber = params.phoneNumber();
        user.hashedPassword = params.hashedPassword();
        user.role = params.role();
        user.twoFactorEnabled = params.twoFactorEnabled();
        user.twoFactorId = params.twoFactorId();
        user.status = params.status();
        user.lastLoginAt = params.lastLoginAt();
        user.createdAt = params.createdAt();
        user.updatedAt = params.updatedAt();

        return user;
    }

    public UserId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public FullName getFullName() {
        return fullName;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public UserRole getRole() {
        return role;
    }

    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public String getTwoFactorId() {
        return twoFactorId;
    }

    public UserStatus getStatus() {
        return status;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
        this.updatedAt = LocalDateTime.now();
    }
}