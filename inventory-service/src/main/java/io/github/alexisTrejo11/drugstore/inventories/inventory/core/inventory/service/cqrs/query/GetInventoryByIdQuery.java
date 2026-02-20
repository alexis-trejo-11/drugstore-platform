package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;

public record GetInventoryByIdQuery(InventoryId inventoryId) {

    public static GetInventoryByIdQuery of(String inventoryId) {
        return new GetInventoryByIdQuery(InventoryId.of(inventoryId));
    }
}
