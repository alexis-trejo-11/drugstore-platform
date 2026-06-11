package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;

public record UpdateInventoryCommand(
        InventoryId inventoryId,
        Integer reorderLevel,
        Integer reorderQuantity,
        Integer maximumStockLevel,
        String warehouseLocation
) {
}
