package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.valueobject;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.SalesOrderItem;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.exception.PurchaseOrderValidationException;

import java.util.List;

@Builder
public record CreateSalesOrderParams(
        SaleOrderId id,
        String notes,
        DeliveryMethod deliveryMethod,
        String customerUserId,
        String deliveryInfoId,
        String pickupInfoId,
        List<SalesOrderItem> items
) {
    public CreateSalesOrderParams {
        if (id == null) {
            throw new PurchaseOrderValidationException("Sales order ID cannot be null.");
        }

        if (items == null) {
            throw new PurchaseOrderValidationException("Sales order items cannot be null.");
        }

        if (items.isEmpty()) {
            throw new PurchaseOrderValidationException("Sales order must contain at least one item.");
        }
    }
}
