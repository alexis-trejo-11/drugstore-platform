package io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects;

import io.github.alexisTrejo11.drugstore.payments.core.domain.validation.DomainValidation;

import java.time.LocalDateTime;

/**
 * Immutable snapshot of a refund operation tied to a Payment.
 * A Payment can have one RefundInfo — if partial refunds are needed in the
 * future,
 * this can be evolved into a list.
 */
public record RefundInfo(
    Money refundedAmount,
    String reason,
    String gatewayRefundId, // e.g. Stripe Refund ID: "re_xxxxx"
    LocalDateTime refundedAt) {

  public RefundInfo {
    DomainValidation.requireNonNull(refundedAmount, "Refunded amount");
    DomainValidation.requireNonBlank(reason, "Refund reason");
    DomainValidation.requireNonNull(refundedAt, "Refunded at");
  }

  public static RefundInfo of(Money amount, String reason, String gatewayRefundId) {
    return new RefundInfo(amount, reason, gatewayRefundId, LocalDateTime.now());
  }
}
