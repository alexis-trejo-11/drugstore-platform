package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.query;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;

public record GetActiveReservationsQuery(InventoryId inventoryId) {
}
