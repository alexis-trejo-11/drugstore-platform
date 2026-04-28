package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.ProductId;

@Builder
public record OrderItemCommand(
        String id,
        ProductId productId,
        String productName,
        Integer quantity
) {
}
