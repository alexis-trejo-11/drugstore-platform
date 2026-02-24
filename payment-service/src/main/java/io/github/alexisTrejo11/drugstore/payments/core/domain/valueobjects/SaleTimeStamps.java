package io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Value object for tracking Sale timestamps throughout its lifecycle.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SaleTimeStamps {
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime cancelledAt;

  public void markAsUpdated() {
    this.updatedAt = LocalDateTime.now();
  }

  public void markAsCancelled() {
    this.cancelledAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public static SaleTimeStamps now() {
    LocalDateTime now = LocalDateTime.now();
    return new SaleTimeStamps(now, now, null);
  }

  public boolean isCancelled() {
    return cancelledAt != null;
  }
}
