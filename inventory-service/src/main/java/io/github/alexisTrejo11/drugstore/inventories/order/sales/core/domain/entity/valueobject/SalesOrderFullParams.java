package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.valueobject;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.SalesOrderItem;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Builder
public record SalesOrderFullParams(
    SaleOrderId id,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String paymentId,
    OrderStatus status,
    String notes,
    DeliveryMethod deliveryMethod,
    String customerUserId,
    String deliveryInfoId,
    String pickupInfoId,
    List<SalesOrderItem> items
) {
    public SalesOrderFullParams {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(items, "Items cannot be null");
    }
}