package io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums;

/**
 * Sale lifecycle states.
 *
 * A Sale represents a confirmed business transaction created from a completed
 * Payment.
 *
 * Flow:
 * CONFIRMED → REFUNDED | PARTIALLY_REFUNDED
 * CONFIRMED → CANCELLED
 */
public enum SaleStatus {
  /**
   * Sale confirmed (created from completed payment).
   */
  CONFIRMED,

  /**
   * Sale was cancelled (usually due to payment refund or business rule).
   */
  CANCELLED,

  /**
   * Sale was fully refunded.
   */
  REFUNDED,

  /**
   * Sale was partially refunded.
   */
  PARTIALLY_REFUNDED
}
