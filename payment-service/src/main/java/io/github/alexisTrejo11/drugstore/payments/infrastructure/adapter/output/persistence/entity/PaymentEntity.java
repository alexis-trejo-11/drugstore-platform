package io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.entity;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.PaymentGateway;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.PaymentMethod;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity for Payment.
 * Lives in infrastructure — the domain aggregate (Payment.java) is completely
 * unaware of this class.
 * Mapping between domain and entity is handled by PaymentMapper.
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payments_order_id", columnList = "order_id", unique = true),
    @Index(name = "idx_payments_customer_id", columnList = "customer_id"),
    @Index(name = "idx_payments_status", columnList = "status"),
    @Index(name = "idx_payments_gateway_payment_id", columnList = "gateway_payment_id")
})
public class PaymentEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false, length = 36)
  private String id;

  @Column(name = "order_id", nullable = false, updatable = false, length = 36)
  private String orderId;

  @Column(name = "customer_id", nullable = false, updatable = false, length = 36)
  private String customerId;

  @Column(name = "amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency;

  @Column(name = "refunded_amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal refundedAmount;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 30)
  private PaymentStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_method", nullable = false, length = 30)
  private PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  @Column(name = "gateway", nullable = false, length = 20)
  private PaymentGateway gateway;

  /** Stripe PaymentIntent ID (pi_xxxxx) */
  @Column(name = "gateway_payment_id", length = 100)
  private String gatewayPaymentId;

  /** Stripe Charge ID (ch_xxxxx) — populated after charge confirmation */
  @Column(name = "gateway_charge_id", length = 100)
  private String gatewayChargeId;

  // ─── Refund fields (embedded) ────────────────────────────────────────────
  @Column(name = "refund_reason")
  private String refundReason;

  @Column(name = "refund_gateway_refund_id", length = 100)
  private String refundGatewayRefundId;

  @Column(name = "refunded_at")
  private LocalDateTime refundedAt;

  // ─── Timestamps ───────────────────────────────────────────────────────────
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Version
  @Column(name = "version", nullable = false)
  private Long version;

  public PaymentEntity() {
  }

  // ─── Getters & Setters ────────────────────────────────────────────────────

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public BigDecimal getRefundedAmount() {
    return refundedAmount;
  }

  public void setRefundedAmount(BigDecimal refundedAmount) {
    this.refundedAmount = refundedAmount;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public PaymentGateway getGateway() {
    return gateway;
  }

  public void setGateway(PaymentGateway gateway) {
    this.gateway = gateway;
  }

  public String getGatewayPaymentId() {
    return gatewayPaymentId;
  }

  public void setGatewayPaymentId(String gatewayPaymentId) {
    this.gatewayPaymentId = gatewayPaymentId;
  }

  public String getGatewayChargeId() {
    return gatewayChargeId;
  }

  public void setGatewayChargeId(String gatewayChargeId) {
    this.gatewayChargeId = gatewayChargeId;
  }

  public String getRefundReason() {
    return refundReason;
  }

  public void setRefundReason(String refundReason) {
    this.refundReason = refundReason;
  }

  public String getRefundGatewayRefundId() {
    return refundGatewayRefundId;
  }

  public void setRefundGatewayRefundId(String refundGatewayRefundId) {
    this.refundGatewayRefundId = refundGatewayRefundId;
  }

  public LocalDateTime getRefundedAt() {
    return refundedAt;
  }

  public void setRefundedAt(LocalDateTime refundedAt) {
    this.refundedAt = refundedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LocalDateTime getCompletedAt() {
    return completedAt;
  }

  public void setCompletedAt(LocalDateTime completedAt) {
    this.completedAt = completedAt;
  }

  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }
}
