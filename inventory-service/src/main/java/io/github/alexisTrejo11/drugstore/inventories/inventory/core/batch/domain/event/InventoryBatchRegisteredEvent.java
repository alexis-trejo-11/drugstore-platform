package io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.event;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.BatchId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;

public record InventoryBatchRegisteredEvent(BatchId batchId, InventoryId inventoryId, Integer quantity) {
}
