package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import org.springframework.data.domain.Pageable;

public record GetInventoryBatchesQuery(InventoryId inventoryId, Boolean activeOnly, Pageable pageable) {
}
