package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.ProductId;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderReference;

import java.util.Map;

@Builder
public record ReserveStockCommand(
        Map<ProductId, Integer> productQuantityMap,
        OrderReference orderReference,
        String reason
) {
}
