package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.ProductId;


public record ReceivedItemDetail(
        ProductId productId,
        String batchNumber,
        int receivedQuantity,
        String supplierId,
        String supplierName) {
}