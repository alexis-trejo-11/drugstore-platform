package io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.mapper;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Sale;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.params.ReconstructSaleParams;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.*;
import io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.entity.SaleEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between the Sale domain aggregate and its JPA entity representation.
 */
@Component
public class SaleMapper {

  // ─── Domain → Entity ──────────────────────────────────────────────────────

  public SaleEntity toEntity(Sale sale) {
    SaleEntity entity = new SaleEntity();

    entity.setId(sale.getId().value());
    entity.setPaymentId(sale.getPaymentId().value());
    entity.setOrderId(sale.getOrderId().value());
    entity.setCustomerId(sale.getCustomerId().value());
    entity.setTotalAmount(sale.getTotalAmount().amount());
    entity.setCurrency(sale.getTotalAmount().currency());
    entity.setRefundedAmount(sale.getRefundedAmount().amount());
    entity.setStatus(sale.getStatus());

    // Timestamps
    SaleTimeStamps ts = sale.getTimeStamps();
    entity.setCreatedAt(ts.getCreatedAt());
    entity.setUpdatedAt(ts.getUpdatedAt());
    entity.setCancelledAt(ts.getCancelledAt());

    return entity;
  }

  // ─── Entity → Domain ──────────────────────────────────────────────────────

  public Sale toDomain(SaleEntity entity) {
    // Reconstruct timestamps
    SaleTimeStamps timeStamps = new SaleTimeStamps(
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getCancelledAt());

    ReconstructSaleParams params = new ReconstructSaleParams(
        SaleID.of(entity.getId()),
        PaymentID.of(entity.getPaymentId()),
        OrderID.of(entity.getOrderId()),
        CustomerID.of(entity.getCustomerId()),
        Money.of(entity.getTotalAmount(), entity.getCurrency()),
        entity.getStatus(),
        Money.of(entity.getRefundedAmount(), entity.getCurrency()),
        timeStamps);

    return Sale.reconstruct(params);
  }
}
