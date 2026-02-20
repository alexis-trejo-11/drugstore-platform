package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.valueobject;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.SalesOrderItem;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.exception.PurchaseOrderValidationException;

import java.util.List;
import java.util.Objects;

@Builder
public record UpdateSalesOrderParams(
    String notes,
    DeliveryMethod deliveryMethod,
    String deliveryInfoId,
    String pickupInfoId,
    List<SalesOrderItem> items
) {
    public UpdateSalesOrderParams {
        Objects.requireNonNull(items, "Items cannot be null");
        if (items.isEmpty()) {
            throw new PurchaseOrderValidationException("Sales order must contain at least one item.");
        }
    }
}