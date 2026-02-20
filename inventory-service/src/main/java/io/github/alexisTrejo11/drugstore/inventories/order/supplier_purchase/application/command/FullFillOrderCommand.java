package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;

import java.util.List;

@Builder
public record FullFillOrderCommand(
        PurchaseOrderId purchaseOrderId,
        List<OrderItemCommand> items,
        String deliveryLocation,
        UserId createdBy
) {
}
