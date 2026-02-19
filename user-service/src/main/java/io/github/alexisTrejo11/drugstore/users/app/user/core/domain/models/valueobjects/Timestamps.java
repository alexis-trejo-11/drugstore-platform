package io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects;

import java.time.LocalDateTime;

/**
 * Value object that encapsulates timestamp fields for User entity
 * Ensures timestamps are always initialized to avoid NPEs
 */
public record Timestamps(
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime lastLoginAt
) {

  /**
   * Factory method to create a Timestamps instance for a new user
   *
   * @return Timestamps with current time for createdAt and updatedAt, null for lastLoginAt
   */
  public static Timestamps none() {
    LocalDateTime now = LocalDateTime.now();
    return new Timestamps(now, now, null);
  }

  /**
   * Factory method to reconstruct Timestamps from persistence data
   *
   * @param createdAt The creation timestamp
   * @param updatedAt The last update timestamp
   * @param lastLoginAt The last login timestamp (can be null)
   * @return A new Timestamps instance
   */
  public static Timestamps of(LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLoginAt) {
    return new Timestamps(createdAt, updatedAt, lastLoginAt);
  }

  /**
   * Updates the lastLoginAt timestamp to current time
   *
   * @return A new Timestamps instance with updated lastLoginAt
   */
  public Timestamps updateLastLogin() {
    return new Timestamps(this.createdAt, LocalDateTime.now(), LocalDateTime.now());
  }

  /**
   * Updates the updatedAt timestamp to current time
   *
   * @return A new Timestamps instance with updated updatedAt
   */
  public Timestamps updateTimestamp() {
    return new Timestamps(this.createdAt, LocalDateTime.now(), this.lastLoginAt);
  }
}

