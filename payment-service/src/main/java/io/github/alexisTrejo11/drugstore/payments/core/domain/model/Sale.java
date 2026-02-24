package io.github.alexisTrejo11.drugstore.payments.core.domain.model;

import io.github.alexisTrejo11.drugstore.payments.core.domain.exception.SaleBusinessRuleException;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.SaleStatus;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.params.ReconstructSaleParams;
import io.github.alexisTrejo11.drugstore.payments.core.domain.validation.DomainValidation;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sale Aggregate Root.
 *
 * A Sale is automatically generated when a Payment transitions to COMPLETED.
 * It represents the confirmed commercial transaction from the business
 * perspective.
 *
 * Lifecycle:
 * CONFIRMED → REFUNDED | PARTIALLY_REFUNDED
 * CONFIRMED → CANCELLED
 */
public class Sale {
  private static final Logger logger = LoggerFactory.getLogger(Sale.class);

  private final SaleID id;
  private final PaymentID paymentId; // Traceability back to the Payment
  private final OrderID orderId;
  private final CustomerID customerId;
  private final Money totalAmount;

  private SaleStatus status;
  private Money refundedAmount;
  private SaleTimeStamps timeStamps;

  private Sale(SaleID id, PaymentID paymentId, OrderID orderId,
      CustomerID customerId, Money totalAmount) {
    this.id = id;
    this.paymentId = paymentId;
    this.orderId = orderId;
    this.customerId = customerId;
    this.totalAmount = totalAmount;
    this.status = SaleStatus.CONFIRMED;
    this.refundedAmount = Money.of(0, totalAmount.currency());
    this.timeStamps = SaleTimeStamps.now();
  }

  // ─── Factory Methods ───────────────────────────────────────────────────────

  /**
   * Creates a Sale from a completed Payment.
   * This is the only valid way to create a Sale — it must come from a Payment.
   */
  public static Sale fromPayment(Payment payment) {
    DomainValidation.requireNonNull(payment, "Payment");

    if (!payment.isCompleted()) {
      throw new SaleBusinessRuleException(
          "Cannot create a Sale from a non-completed Payment. Payment status: " + payment.getStatus());
    }

    SaleID id = SaleID.generate();
    Sale sale = new Sale(id, payment.getId(), payment.getOrderId(),
        payment.getCustomerId(), payment.getAmount());

    logger.info("Sale created from payment | saleId={} paymentId={} orderId={} customerId={} amount={}",
        id.value(), payment.getId().value(), payment.getOrderId().value(),
        payment.getCustomerId().value(), payment.getAmount());
    return sale;
  }

  public static Sale reconstruct(ReconstructSaleParams params) {
    DomainValidation.requireNonNull(params, "ReconstructSaleParams");
    DomainValidation.requireNonNull(params.id(), "SaleID");
    DomainValidation.requireNonNull(params.paymentId(), "PaymentID");
    DomainValidation.requireNonNull(params.orderId(), "OrderID");
    DomainValidation.requireNonNull(params.customerId(), "CustomerID");
    DomainValidation.requireNonNull(params.status(), "SaleStatus");
    DomainValidation.requireNonNull(params.totalAmount(), "Total amount");

    Sale sale = new Sale(params.id(), params.paymentId(), params.orderId(),
        params.customerId(), params.totalAmount());
    sale.status = params.status();
    sale.refundedAmount = params.refundedAmount() != null
        ? params.refundedAmount()
        : Money.of(0, params.totalAmount().currency());
    if (params.timeStamps() != null) {
      sale.timeStamps = params.timeStamps();
    }

    logger.debug("Sale reconstructed | id={} status={}", params.id().value(), params.status());
    return sale;
  }

  // ─── Business Methods ──────────────────────────────────────────────────────

  /**
   * Cancels the sale. Should only be called when the underlying payment is
   * refunded or fails.
   */
  public void cancel(String reason) {
    if (status == SaleStatus.CANCELLED) {
      throw new SaleBusinessRuleException("Sale is already cancelled");
    }
    DomainValidation.requireNonBlank(reason, "Cancellation reason");

    this.status = SaleStatus.CANCELLED;
    this.timeStamps.markAsCancelled();

    logger.info("Sale cancelled | id={} orderId={} reason={}", id.value(), orderId.value(), reason);
  }

  /**
   * Registers a full refund on the sale side.
   * Called after the underlying Payment has been refunded.
   */
  public void registerFullRefund() {
    if (status != SaleStatus.CONFIRMED && status != SaleStatus.PARTIALLY_REFUNDED) {
      throw new SaleBusinessRuleException(
          "Cannot refund a Sale with status: " + status);
    }

    this.refundedAmount = this.totalAmount;
    this.status = SaleStatus.REFUNDED;
    this.timeStamps.markAsUpdated();

    logger.info("Sale fully refunded | id={} amount={}", id.value(), totalAmount);
  }

  /**
   * Registers a partial refund on the sale side.
   */
  public void registerPartialRefund(Money refundAmount) {
    if (status != SaleStatus.CONFIRMED && status != SaleStatus.PARTIALLY_REFUNDED) {
      throw new SaleBusinessRuleException(
          "Cannot partially refund a Sale with status: " + status);
    }
    DomainValidation.requireNonNull(refundAmount, "Refund amount");
    DomainValidation.requirePositive(refundAmount.amount(), "Refund amount");

    Money totalRefunded = this.refundedAmount.add(refundAmount);
    if (totalRefunded.isGreaterThan(this.totalAmount)) {
      throw new SaleBusinessRuleException(
          "Refunded amount " + totalRefunded + " exceeds sale total " + this.totalAmount);
    }

    this.refundedAmount = totalRefunded;
    this.status = SaleStatus.PARTIALLY_REFUNDED;
    this.timeStamps.markAsUpdated();

    logger.info("Sale partially refunded | id={} refundAmount={} totalRefunded={}",
        id.value(), refundAmount, totalRefunded);
  }

  // ─── Query Methods ─────────────────────────────────────────────────────────

  public boolean isActive() {
    return status == SaleStatus.CONFIRMED || status == SaleStatus.PARTIALLY_REFUNDED;
  }

  public boolean isFullyRefunded() {
    return status == SaleStatus.REFUNDED;
  }

  public Money getNetAmount() {
    return totalAmount.subtract(refundedAmount);
  }

  // ─── Getters ───────────────────────────────────────────────────────────────

  public SaleID getId() {
    return id;
  }

  public PaymentID getPaymentId() {
    return paymentId;
  }

  public OrderID getOrderId() {
    return orderId;
  }

  public CustomerID getCustomerId() {
    return customerId;
  }

  public Money getTotalAmount() {
    return totalAmount;
  }

  public SaleStatus getStatus() {
    return status;
  }

  public Money getRefundedAmount() {
    return refundedAmount;
  }

  public SaleTimeStamps getTimeStamps() {
    return timeStamps;
  }
}