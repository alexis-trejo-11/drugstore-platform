package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.event;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.ProductId;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderReference;

import java.util.Map;

public record ReceiveSaleOrderEvent(Map<ProductId, Integer> productQuantityMap, OrderReference orderReference) {
}
