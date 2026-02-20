package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.entity;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.enums.AdjustmentReason;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.enums.AdjustmentType;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.BatchId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.StockAdjustment;

import java.time.LocalDateTime;

public class StockAdjustmentFactory {

    public static StockAdjustment create(InventoryId inventoryId, BatchId batchId, Integer quantityBefore,
                                         Integer quantityAdjusted, AdjustmentReason reason,
                                         String notes, UserId approvedBy, UserId performedBy) {
        AdjustmentType adjustmentType = quantityAdjusted > 0 ? AdjustmentType.INCREASE : AdjustmentType.DECREASE;
        Integer quantityAfter = quantityBefore + quantityAdjusted;

        return StockAdjustment.reconstructor()
                .id(AdjustmentId.generate())
                .inventoryId(inventoryId)
                .batchId(batchId)
                .adjustmentType(adjustmentType)
                .quantityBefore(quantityBefore)
                .quantityAdjusted(Math.abs(quantityAdjusted))
                .quantityAfter(quantityAfter)
                .reason(reason)
                .notes(notes)
                .approvedBy(approvedBy)
                .performedBy(performedBy)
                .adjustmentDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .reconstruct();
    }
}
