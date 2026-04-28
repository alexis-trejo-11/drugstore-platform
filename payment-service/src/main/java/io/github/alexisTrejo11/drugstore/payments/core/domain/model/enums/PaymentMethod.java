package io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums;

/**
 * Types of payment methods accepted by the platform.
 */
public enum PaymentMethod {
  /**
   * Credit card payment (Visa, Mastercard, etc.).
   */
  CREDIT_CARD,

  /**
   * Debit card payment.
   */
  DEBIT_CARD,

  /**
   * Cash payment (in-store).
   */
  CASH,

  /**
   * Internal credit balance (store credit, gift cards, etc.).
   */
  INTERNAL_CREDIT,

  /**
   * Bank transfer.
   */
  BANK_TRANSFER,

  /**
   * Digital wallet (PayPal, Apple Pay, Google Pay, etc.).
   */
  DIGITAL_WALLET,

  /**
   * Other or unknown payment method.
   */
  OTHER
}
