package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.enums.AdjustmentReason;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.BatchId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;

@Builder
public record AdjustInventoryCommand(
        InventoryId inventoryId,
        BatchId batchId,
        Integer quantityAdjustment,
        AdjustmentReason reason,
        String notes,
        UserId performedBy,
        UserId approvedBy
) {
}