package microservice.inventory_service.internal.core.inventory.application.cqrs.command;

import lombok.Builder;
import microservice.inventory_service.internal.core.inventory.domain.entity.enums.AdjustmentReason;
import microservice.inventory_service.internal.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;

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