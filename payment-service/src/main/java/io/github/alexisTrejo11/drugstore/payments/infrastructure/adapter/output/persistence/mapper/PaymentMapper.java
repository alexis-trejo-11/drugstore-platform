package io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.mapper;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Payment;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.PaymentGateway;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.params.ReconstructPaymentParams;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.*;
import io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.entity.PaymentEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between the Payment domain aggregate and its JPA entity representation.
 *
 * Rules:
 * - toEntity() → domain aggregate → JPA entity (for persistence)
 * - toDomain() → JPA entity → domain aggregate (via reconstruct)
 *
 * No third-party mapping library (MapStruct) is used intentionally — the
 * mapping
 * is explicit and visible, which matters when value objects have validation
 * logic.
 */
@Component
public class PaymentMapper {

  // ─── Domain → Entity ──────────────────────────────────────────────────────

  public PaymentEntity toEntity(Payment payment) {
    PaymentEntity entity = new PaymentEntity();

    entity.setId(payment.getId().value());
    entity.setOrderId(payment.getOrderId().value());
    entity.setCustomerId(payment.getCustomerId().value());
    entity.setAmount(payment.getAmount().amount());
    entity.setCurrency(payment.getAmount().currency());
    entity.setRefundedAmount(payment.getRefundedAmount().amount());
    entity.setStatus(payment.getStatus());
    entity.setPaymentMethod(payment.getPaymentMethod());

    // Gateway reference
    PaymentGatewayRef gatewayRef = payment.getGatewayRef();
    entity.setGateway(gatewayRef.gateway());
    entity.setGatewayPaymentId(
        PaymentGatewayRef.NONE.equals(gatewayRef) ? null : gatewayRef.gatewayPaymentId());
    entity.setGatewayChargeId(gatewayRef.gatewayChargeId());

    // Refund info (nullable — only present after a refund)
    RefundInfo refundInfo = payment.getRefundInfo();
    if (refundInfo != null) {
      entity.setRefundReason(refundInfo.reason());
      entity.setRefundGatewayRefundId(refundInfo.gatewayRefundId());
      entity.setRefundedAt(refundInfo.refundedAt());
    }

    // Timestamps
    PaymentTimeStamps ts = payment.getTimeStamps();
    entity.setCreatedAt(ts.getCreatedAt());
    entity.setUpdatedAt(ts.getUpdatedAt());
    entity.setCompletedAt(ts.getCompletedAt());
    entity.setDeletedAt(ts.getDeletedAt());

    return entity;
  }

  // ─── Entity → Domain ──────────────────────────────────────────────────────

  public Payment toDomain(PaymentEntity entity) {
    // Reconstruct gateway reference
    PaymentGatewayRef gatewayRef = buildGatewayRef(entity);

    // Reconstruct refund info (nullable)
    RefundInfo refundInfo = buildRefundInfo(entity);

    // Reconstruct timestamps
    PaymentTimeStamps timeStamps = PaymentTimeStamps.of(
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getCompletedAt(),
        entity.getDeletedAt());

    ReconstructPaymentParams params = new ReconstructPaymentParams(
        PaymentID.of(entity.getId()),
        OrderID.of(entity.getOrderId()),
        CustomerID.of(entity.getCustomerId()),
        Money.of(entity.getAmount(), entity.getCurrency()),
        entity.getPaymentMethod(),
        entity.getStatus(),
        gatewayRef,
        Money.of(entity.getRefundedAmount(), entity.getCurrency()),
        refundInfo,
        timeStamps);

    return Payment.reconstruct(params);
  }

  // ─── Private helpers ──────────────────────────────────────────────────────

  private PaymentGatewayRef buildGatewayRef(PaymentEntity entity) {
    if (entity.getGateway() == null || entity.getGateway() == PaymentGateway.NONE) {
      return PaymentGatewayRef.NONE;
    }
    if (entity.getGatewayPaymentId() == null || entity.getGatewayPaymentId().isBlank()) {
      return PaymentGatewayRef.NONE;
    }
    return new PaymentGatewayRef(
        entity.getGateway(),
        entity.getGatewayPaymentId(),
        entity.getGatewayChargeId());
  }

  private RefundInfo buildRefundInfo(PaymentEntity entity) {
    if (entity.getRefundReason() == null) {
      return null;
    }
    // Reconstruct the refunded amount (same currency as payment)
    Money refundedMoney = Money.of(entity.getRefundedAmount(), entity.getCurrency());

    return new RefundInfo(
        refundedMoney,
        entity.getRefundReason(),
        entity.getRefundGatewayRefundId(),
        entity.getRefundedAt());
  }
}
