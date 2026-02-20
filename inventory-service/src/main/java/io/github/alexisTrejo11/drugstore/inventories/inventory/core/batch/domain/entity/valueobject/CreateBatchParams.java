package io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;

import java.time.LocalDateTime;

@Builder
public record CreateBatchParams(
        InventoryId inventoryId, String batchNumber, String lotNumber,
        Integer quantity,
        LocalDateTime manufacturingDate, LocalDateTime expirationDate,
        String supplierId, String supplierName, String storageConditions
) {
}


