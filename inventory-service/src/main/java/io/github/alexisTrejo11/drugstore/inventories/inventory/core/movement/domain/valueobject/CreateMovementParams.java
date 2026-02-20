package io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.valueobject;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.BatchId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;

@Builder
public record CreateMovementParams(
        InventoryId inventoryId, BatchId batchId, MovementType movementType,
        Integer quantity, Integer previousQuantity, Integer newQuantity,
        String reason, String referenceId, String referenceType,
        UserId performedBy, String notes
) {

    public static CreateMovementParams batchMovement(
            InventoryId inventoryId, BatchId batchId,
            Integer quantity, Integer previousQuantity, Integer newQuantity,
            String referenceId, MovementType movementType
    ) {
        return CreateMovementParams.builder()
                .inventoryId(inventoryId)
                .batchId(batchId)
                .batchId(batchId)
                .quantity(quantity)
                .movementType(movementType)
                .previousQuantity(previousQuantity)
                .newQuantity(newQuantity)
                .referenceId(referenceId)
                .build();
    }
}
