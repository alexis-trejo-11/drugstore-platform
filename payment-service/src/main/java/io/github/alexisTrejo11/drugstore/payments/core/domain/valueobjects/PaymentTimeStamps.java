package io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Value object for tracking Payment timestamps throughout its lifecycle.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTimeStamps {
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime completedAt;
  private LocalDateTime deletedAt;

  public void markAsUpdated() {
    this.updatedAt = LocalDateTime.now();
  }

  public void markAsCompleted() {
    this.completedAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public void markAsDeleted() {
    this.deletedAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public static PaymentTimeStamps now() {
    LocalDateTime now = LocalDateTime.now();
    return new PaymentTimeStamps(now, now, null, null);
  }

  public static PaymentTimeStamps of(LocalDateTime createdAt, LocalDateTime updatedAt,
      LocalDateTime completedAt, LocalDateTime deletedAt) {
    return new PaymentTimeStamps(createdAt, updatedAt, completedAt, deletedAt);
  }

  public boolean isCompleted() {
    return completedAt != null;
  }

  public boolean isDeleted() {
    return deletedAt != null;
  }
}
