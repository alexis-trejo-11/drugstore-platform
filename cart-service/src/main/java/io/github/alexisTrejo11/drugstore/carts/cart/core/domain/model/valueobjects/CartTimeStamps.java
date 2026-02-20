package io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects;

import java.time.LocalDateTime;

/**
 * Value object representing timestamps for cart entities.
 * Tracks creation, update, and deletion times.
 */
public class CartTimeStamps {

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;

  private CartTimeStamps(LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.deletedAt = deletedAt;
  }

  /**
   * Creates a new CartTimeStamps with current time for creation and update.
   *
   * @return a new CartTimeStamps initialized to now
   */
  public static CartTimeStamps now() {
    LocalDateTime now = LocalDateTime.now();
    return new CartTimeStamps(now, now, null);
  }

  /**
   * Reconstructs CartTimeStamps from existing values.
   *
   * @param createdAt the creation timestamp
   * @param updatedAt the last update timestamp
   * @param deletedAt the deletion timestamp (can be null)
   * @return reconstructed CartTimeStamps
   */
  public static CartTimeStamps reconstruct(LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
    return new CartTimeStamps(
        createdAt != null ? createdAt : LocalDateTime.now(),
        updatedAt != null ? updatedAt : LocalDateTime.now(),
        deletedAt);
  }

  /**
   * Marks the entity as updated at the current time.
   */
  public void markAsUpdated() {
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Marks the entity as deleted at the current time.
   */
  public void markAsDeleted() {
    this.deletedAt = LocalDateTime.now();
  }

  /**
   * Restores a deleted entity by clearing the deletion timestamp.
   */
  public void restore() {
    this.deletedAt = null;
  }

  /**
   * Checks if the entity has been soft-deleted.
   *
   * @return true if the entity is deleted
   */
  public boolean isDeleted() {
    return this.deletedAt != null;
  }

  // Getters
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }

  @Override
  public String toString() {
    return String.format("CartTimeStamps{createdAt=%s, updatedAt=%s, deletedAt=%s}",
        createdAt, updatedAt, deletedAt);
  }
}
