package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.ProductId;

public record GetInventoryByProductQuery(ProductId productId) {
    public static GetInventoryByProductQuery of(String productId) {
        return new GetInventoryByProductQuery(ProductId.of(productId));
    }
}
