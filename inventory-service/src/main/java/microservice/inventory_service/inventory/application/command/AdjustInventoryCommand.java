package microservice.inventory_service.inventory.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.domain.entity.enums.AdjustmentReason;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;

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