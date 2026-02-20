package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.OrderStatus;

public record UpdatePurchaseOrderStatusCommand(PurchaseOrderId purchaseOrderId, OrderStatus newStatus, UserId updatedBy) {
}
