package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command;

import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;

public record DeletePurchaseOrderCommand(
        PurchaseOrderId purchaseOrderId,
        boolean hardDelete
) {
}
