package io.github.alexisTrejo11.drugstore.users.user.core.domain.models.entities;

import io.github.alexisTrejo11.drugstore.users.user.core.domain.exceptions.UserDisableError;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserRole;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserStatus;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.FullName;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.Timestamps;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.TwoFactorConfig;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;

public class User {
  // Identity
  private UserId id;

  // Contact Information
  private Email email;
  private FullName fullName;
  private PhoneNumber phoneNumber;

  // Security
  private String hashedPassword;
  private UserRole role;
  private UserStatus status;

  // Two-Factor Authentication Configuration
  private TwoFactorConfig twoFactorConfig;

  // Timestamps
  private Timestamps timestamps;

  public void updateLastLogin() {
    this.timestamps = this.timestamps.updateLastLogin();
  }

  public void validateNotDisabled() {
    if (this.status == UserStatus.INACTIVE || this.status == UserStatus.SUSPENDED
        || this.status == UserStatus.DELETED) {
      throw new UserDisableError("User account is " + this.status);
    }
  }

  public boolean isTwoFactorEnabled() {
    return twoFactorConfig != null && twoFactorConfig.isActive();
  }

  public void enableTwoFactor(String twoFactorId) {
    this.twoFactorConfig = twoFactorConfig.enable(twoFactorId);
    this.timestamps = this.timestamps.updateTimestamp();
  }

  public void disableTwoFactor() {
    this.twoFactorConfig = twoFactorConfig.disable();
    this.timestamps = this.timestamps.updateTimestamp();
  }

  public void activate() {
    if (this.status != UserStatus.PENDING) {
      throw new IllegalStateException("User account is not pending activation.");
    }
    this.status = UserStatus.ACTIVE;
    this.timestamps = this.timestamps.updateTimestamp();
  }

  public void deactivate() {
    this.status = UserStatus.INACTIVE;
    this.timestamps = this.timestamps.updateTimestamp();
  }

  public void ban() {
    if (this.status == UserStatus.SUSPENDED) {
      throw new IllegalStateException("User account is already suspended.");
    }
    this.status = UserStatus.SUSPENDED;
    this.timestamps = this.timestamps.updateTimestamp();
  }

  public void unban() {
    if (this.status != UserStatus.SUSPENDED) {
      throw new IllegalStateException("User account is not suspended.");
    }
    this.status = UserStatus.ACTIVE;
    this.timestamps = this.timestamps.updateTimestamp();
  }

  public void softDelete() {
    this.status = UserStatus.DELETED;
    this.timestamps = this.timestamps.updateTimestamp();
  }

  public void restore() {
    this.status = UserStatus.ACTIVE;
    this.timestamps = this.timestamps.updateTimestamp();
  }

  public void updateAuthFields(Email email, PhoneNumber phoneNumber) {
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.timestamps = this.timestamps.updateTimestamp();
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
    user.twoFactorConfig = TwoFactorConfig.none();
    user.status = UserStatus.PENDING;
    user.timestamps = Timestamps.none();

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
    user.twoFactorConfig = TwoFactorConfig.of(
        params.twoFactorEnabled() != null ? params.twoFactorEnabled() : false,
        params.twoFactorId()
    );
    user.status = params.status();
    user.timestamps = Timestamps.of(params.createdAt(), params.updatedAt(), params.lastLoginAt());

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

  public TwoFactorConfig getTwoFactorConfig() {
    return twoFactorConfig;
  }

  public UserStatus getStatus() {
    return status;
  }

  public Timestamps getTimestamps() {
    return timestamps;
  }

  // Convenience getters for timestamps
  public java.time.LocalDateTime getLastLoginAt() {
    return timestamps != null ? timestamps.lastLoginAt() : null;
  }

  public java.time.LocalDateTime getCreatedAt() {
    return timestamps != null ? timestamps.createdAt() : null;
  }

  public java.time.LocalDateTime getUpdatedAt() {
    return timestamps != null ? timestamps.updatedAt() : null;
  }

  public void setHashedPassword(String hashedPassword) {
    this.hashedPassword = hashedPassword;
    this.timestamps = this.timestamps.updateTimestamp();
  }
}