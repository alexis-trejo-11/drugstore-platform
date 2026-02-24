package io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.entity;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.SaleStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity for Sale.
 * Intentionally flat — no @OneToOne to PaymentEntity to keep aggregates
 * independently loadable without JPA joins. The paymentId is a plain FK string.
 */
@Entity
@Table(name = "sales", indexes = {
    @Index(name = "idx_sales_payment_id", columnList = "payment_id", unique = true),
    @Index(name = "idx_sales_order_id", columnList = "order_id", unique = true),
    @Index(name = "idx_sales_customer_id", columnList = "customer_id"),
    @Index(name = "idx_sales_status", columnList = "status")
})
public class SaleEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false, length = 36)
  private String id;

  /** FK reference — no JPA relationship intentionally (aggregate boundary) */
  @Column(name = "payment_id", nullable = false, updatable = false, length = 36)
  private String paymentId;

  @Column(name = "order_id", nullable = false, updatable = false, length = 36)
  private String orderId;

  @Column(name = "customer_id", nullable = false, updatable = false, length = 36)
  private String customerId;

  @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal totalAmount;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency;

  @Column(name = "refunded_amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal refundedAmount;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 30)
  private SaleStatus status;

  // ─── Timestamps ───────────────────────────────────────────────────────────
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "cancelled_at")
  private LocalDateTime cancelledAt;

  @Version
  @Column(name = "version", nullable = false)
  private Long version;

  public SaleEntity() {
  }

  // ─── Getters & Setters ────────────────────────────────────────────────────

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
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

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
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

  public SaleStatus getStatus() {
    return status;
  }

  public void setStatus(SaleStatus status) {
    this.status = status;
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

  public LocalDateTime getCancelledAt() {
    return cancelledAt;
  }

  public void setCancelledAt(LocalDateTime cancelledAt) {
    this.cancelledAt = cancelledAt;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }
}
