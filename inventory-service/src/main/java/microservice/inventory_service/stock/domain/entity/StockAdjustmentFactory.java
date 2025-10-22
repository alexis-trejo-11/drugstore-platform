package microservice.inventory_service.stock.domain.entity;

import microservice.inventory_service.inventory.domain.entity.enums.AdjustmentReason;
import microservice.inventory_service.inventory.domain.entity.enums.AdjustmentType;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.AdjustmentId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;
import microservice.inventory_service.stock.domain.StockAdjustment;

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
