package io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums;

/**
 * Payment lifecycle states.
 *
 * Flow:
 * PENDING → PROCESSING → COMPLETED → (REFUNDED | PARTIALLY_REFUNDED)
 * PENDING → CANCELLED
 * PROCESSING → FAILED
 */
public enum PaymentStatus {
  /**
   * Payment has been created but not yet sent to the gateway.
   */
  PENDING,

  /**
   * Payment is being processed by the gateway (e.g., Stripe PaymentIntent
   * created).
   */
  PROCESSING,

  /**
   * Payment successfully completed and confirmed by the gateway.
   */
  COMPLETED,

  /**
   * Payment processing failed (declined, error, etc.).
   */
  FAILED,

  /**
   * Payment was cancelled before processing.
   */
  CANCELLED,

  /**
   * Payment was fully refunded after being completed.
   */
  REFUNDED,

  /**
   * Payment was partially refunded after being completed.
   */
  PARTIALLY_REFUNDED
}
