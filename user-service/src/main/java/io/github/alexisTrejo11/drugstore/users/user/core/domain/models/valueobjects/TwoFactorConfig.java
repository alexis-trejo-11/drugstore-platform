package io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects;

/**
 * Value object that encapsulates two-factor authentication configuration
 * Ensures two-factor config is always initialized to avoid NPEs
 */
public record TwoFactorConfig(
    Boolean enabled,
    String twoFactorId
) {

  /**
   * Factory method to create a default TwoFactorConfig instance (disabled)
   *
   * @return TwoFactorConfig with disabled state and null twoFactorId
   */
  public static TwoFactorConfig none() {
    return new TwoFactorConfig(false, null);
  }

  /**
   * Factory method to create a TwoFactorConfig instance from data
   *
   * @param enabled Whether two-factor authentication is enabled
   * @param twoFactorId The two-factor ID (usually from an authenticator app or SMS service)
   * @return A new TwoFactorConfig instance
   */
  public static TwoFactorConfig of(Boolean enabled, String twoFactorId) {
    return new TwoFactorConfig(enabled != null ? enabled : false, twoFactorId);
  }

  /**
   * Checks if two-factor authentication is active
   *
   * @return true if enabled and has a valid twoFactorId
   */
  public boolean isActive() {
    return enabled != null && enabled && twoFactorId != null && !twoFactorId.isEmpty();
  }

  /**
   * Enable two-factor authentication with the provided ID
   *
   * @param twoFactorId The two-factor ID
   * @return A new TwoFactorConfig instance with enabled state
   */
  public TwoFactorConfig enable(String twoFactorId) {
    if (twoFactorId == null || twoFactorId.trim().isEmpty()) {
      throw new IllegalArgumentException("Two-factor ID cannot be null or empty");
    }
    return new TwoFactorConfig(true, twoFactorId);
  }

  /**
   * Disable two-factor authentication
   *
   * @return A new TwoFactorConfig instance with disabled state
   */
  public TwoFactorConfig disable() {
    return new TwoFactorConfig(false, null);
  }
}

