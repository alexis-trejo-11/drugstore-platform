package io.github.alexisTrejo11.drugstore.payments.core.domain.model;

import io.github.alexisTrejo11.drugstore.payments.core.domain.exception.PaymentBusinessRuleException;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.PaymentMethod;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.PaymentStatus;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.params.CreatePaymentParams;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.params.ReconstructPaymentParams;
import io.github.alexisTrejo11.drugstore.payments.core.domain.validation.DomainValidation;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Payment Aggregate Root.
 *
 * Lifecycle:
 * PENDING → PROCESSING → COMPLETED → (REFUNDED | PARTIALLY_REFUNDED)
 * → FAILED
 * PENDING → CANCELLED
 *
 * A completed Payment triggers the creation of a Sale (handled at application
 * layer).
 * Gateway details (Stripe PaymentIntent, Charge IDs) are tracked via
 * PaymentGatewayRef.
 */
public class Payment {
  private static final Logger logger = LoggerFactory.getLogger(Payment.class);

  private final PaymentID id;
  private final OrderID orderId;
  private final CustomerID customerId;
  private final Money amount;
  private final PaymentMethod paymentMethod;

  private PaymentStatus status;
  private PaymentGatewayRef gatewayRef;
  private Money refundedAmount;
  private RefundInfo refundInfo;
  private PaymentTimeStamps timeStamps;

  private Payment(PaymentID id, OrderID orderId, CustomerID customerId,
      Money amount, PaymentMethod paymentMethod) {
    this.id = id;
    this.orderId = orderId;
    this.customerId = customerId;
    this.amount = amount;
    this.paymentMethod = paymentMethod;
    this.status = PaymentStatus.PENDING;
    this.gatewayRef = PaymentGatewayRef.NONE;
    this.refundedAmount = Money.of(0, amount.currency());
    this.refundInfo = null;
    this.timeStamps = PaymentTimeStamps.now();
  }

  // ─── Factory Methods ───────────────────────────────────────────────────────

  public static Payment create(CreatePaymentParams params) {
    DomainValidation.requireNonNull(params, "CreatePaymentParams");
    DomainValidation.requireNonNull(params.orderId(), "OrderID");
    DomainValidation.requireNonNull(params.customerId(), "CustomerID");
    DomainValidation.requireNonNull(params.amount(), "Amount");
    DomainValidation.requireNonNull(params.paymentMethod(), "PaymentMethod");
    DomainValidation.requirePositive(params.amount().amount(), "Payment amount");

    PaymentID id = PaymentID.generate();
    Payment payment = new Payment(id, params.orderId(), params.customerId(),
        params.amount(), params.paymentMethod());

    logger.info("Payment created | id={} orderId={} customerId={} amount={} method={}",
        id.value(), params.orderId().value(), params.customerId().value(),
        params.amount(), params.paymentMethod());
    return payment;
  }

  public static Payment reconstruct(ReconstructPaymentParams params) {
    DomainValidation.requireNonNull(params, "ReconstructPaymentParams");
    DomainValidation.requireNonNull(params.id(), "PaymentID");
    DomainValidation.requireNonNull(params.orderId(), "OrderID");
    DomainValidation.requireNonNull(params.customerId(), "CustomerID");
    DomainValidation.requireNonNull(params.status(), "PaymentStatus");
    DomainValidation.requireNonNull(params.amount(), "Amount");

    Payment payment = new Payment(params.id(), params.orderId(), params.customerId(),
        params.amount(), params.paymentMethod());
    payment.status = params.status();
    payment.gatewayRef = params.gatewayRef() != null ? params.gatewayRef() : PaymentGatewayRef.NONE;
    payment.refundedAmount = params.refundedAmount() != null
        ? params.refundedAmount()
        : Money.of(0, params.amount().currency());
    payment.refundInfo = params.refundInfo();
    if (params.timeStamps() != null) {
      payment.timeStamps = params.timeStamps();
    }

    logger.debug("Payment reconstructed | id={} status={}", params.id().value(), params.status());
    return payment;
  }

  // ─── Business Methods ──────────────────────────────────────────────────────

  /**
   * Registers the gateway reference once the payment intent is created on Stripe.
   * Transitions status to PROCESSING.
   */
  public void startProcessing(PaymentGatewayRef gatewayRef) {
    if (status != PaymentStatus.PENDING) {
      throw new PaymentBusinessRuleException(
          "Payment can only start processing from PENDING state. Current: " + status);
    }
    DomainValidation.requireNonNull(gatewayRef, "PaymentGatewayRef");

    this.gatewayRef = gatewayRef;
    this.status = PaymentStatus.PROCESSING;
    this.timeStamps.markAsUpdated();

    logger.info("Payment processing started | id={} gateway={} gatewayPaymentId={}",
        id.value(), gatewayRef.gateway(), gatewayRef.gatewayPaymentId());
  }

  /**
   * Marks payment as COMPLETED after gateway confirms the charge.
   * Optionally registers the charge ID (Stripe charge ID).
   * After this, the application layer should create a Sale.
   */
  public void complete(String gatewayChargeId) {
    if (status != PaymentStatus.PROCESSING) {
      throw new PaymentBusinessRuleException(
          "Payment can only be completed from PROCESSING state. Current: " + status);
    }

    // Enrich gateway ref with charge ID if provided
    if (gatewayChargeId != null && !gatewayChargeId.isBlank()) {
      this.gatewayRef = new PaymentGatewayRef(
          gatewayRef.gateway(),
          gatewayRef.gatewayPaymentId(),
          gatewayChargeId);
    }

    this.status = PaymentStatus.COMPLETED;
    this.timeStamps.markAsCompleted();

    logger.info("Payment completed | id={} orderId={} amount={} chargeId={}",
        id.value(), orderId.value(), amount, gatewayChargeId);
  }

  /**
   * Marks payment as FAILED after gateway rejects it or an error occurs.
   */
  public void fail(String reason) {
    if (status != PaymentStatus.PROCESSING && status != PaymentStatus.PENDING) {
      throw new PaymentBusinessRuleException(
          "Payment can only fail from PENDING or PROCESSING state. Current: " + status);
    }
    DomainValidation.requireNonBlank(reason, "Failure reason");

    this.status = PaymentStatus.FAILED;
    this.timeStamps.markAsUpdated();

    logger.warn("Payment failed | id={} orderId={} reason={}", id.value(), orderId.value(), reason);
  }

  /**
   * Cancels a payment that hasn't been processed yet.
   */
  public void cancel() {
    if (status != PaymentStatus.PENDING) {
      throw new PaymentBusinessRuleException(
          "Only PENDING payments can be cancelled. Current: " + status);
    }

    this.status = PaymentStatus.CANCELLED;
    this.timeStamps.markAsUpdated();

    logger.info("Payment cancelled | id={} orderId={}", id.value(), orderId.value());
  }

  /**
   * Processes a full refund. The application layer should update the Sale
   * accordingly.
   */
  public void refund(String reason, String gatewayRefundId) {
    if (status != PaymentStatus.COMPLETED) {
      throw new PaymentBusinessRuleException(
          "Only COMPLETED payments can be refunded. Current: " + status);
    }
    DomainValidation.requireNonBlank(reason, "Refund reason");

    this.refundedAmount = this.amount;
    this.refundInfo = RefundInfo.of(this.amount, reason, gatewayRefundId);
    this.status = PaymentStatus.REFUNDED;
    this.timeStamps.markAsUpdated();

    logger.info("Payment fully refunded | id={} amount={} refundId={}",
        id.value(), amount, gatewayRefundId);
  }

  /**
   * Processes a partial refund.
   */
  public void partialRefund(Money refundAmount, String reason, String gatewayRefundId) {
    if (status != PaymentStatus.COMPLETED && status != PaymentStatus.PARTIALLY_REFUNDED) {
      throw new PaymentBusinessRuleException(
          "Only COMPLETED or PARTIALLY_REFUNDED payments can have partial refunds. Current: " + status);
    }
    DomainValidation.requireNonNull(refundAmount, "Refund amount");
    DomainValidation.requirePositive(refundAmount.amount(), "Refund amount");
    DomainValidation.requireNonBlank(reason, "Refund reason");

    Money totalRefunded = this.refundedAmount.add(refundAmount);
    if (totalRefunded.isGreaterThan(this.amount)) {
      throw new PaymentBusinessRuleException(
          "Total refunded amount " + totalRefunded + " exceeds original payment " + this.amount);
    }

    this.refundedAmount = totalRefunded;
    this.refundInfo = RefundInfo.of(refundAmount, reason, gatewayRefundId);
    this.status = PaymentStatus.PARTIALLY_REFUNDED;
    this.timeStamps.markAsUpdated();

    logger.info("Payment partially refunded | id={} refundAmount={} totalRefunded={} refundId={}",
        id.value(), refundAmount, totalRefunded, gatewayRefundId);
  }

  // ─── Query Methods ─────────────────────────────────────────────────────────

  public boolean isCompleted() {
    return status == PaymentStatus.COMPLETED;
  }

  public boolean isRefundable() {
    return status == PaymentStatus.COMPLETED || status == PaymentStatus.PARTIALLY_REFUNDED;
  }

  public boolean isPending() {
    return status == PaymentStatus.PENDING;
  }

  public Money getRemainingRefundableAmount() {
    return amount.subtract(refundedAmount);
  }

  // ─── Getters ───────────────────────────────────────────────────────────────

  public PaymentID getId() {
    return id;
  }

  public OrderID getOrderId() {
    return orderId;
  }

  public CustomerID getCustomerId() {
    return customerId;
  }

  public Money getAmount() {
    return amount;
  }

  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public PaymentGatewayRef getGatewayRef() {
    return gatewayRef;
  }

  public Money getRefundedAmount() {
    return refundedAmount;
  }

  public RefundInfo getRefundInfo() {
    return refundInfo;
  }

  public PaymentTimeStamps getTimeStamps() {
    return timeStamps;
  }
}
