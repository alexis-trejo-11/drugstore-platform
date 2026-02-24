package io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums;

/**
 * External payment gateway providers.
 */
public enum PaymentGateway {
  /**
   * Stripe payment gateway.
   */
  STRIPE,

  /**
   * PayPal payment gateway.
   */
  PAYPAL,

  /**
   * Manual/offline payment (cash, bank transfer recorded manually).
   */
  MANUAL,

  /**
   * No gateway (placeholder for non-processed payments).
   */
  NONE,

  /**
   * Other or custom payment gateway.
   */
  OTHER
}
