package microservice.inventory_service.internal.inventory.core.movement.domain.valueobject;

import lombok.Builder;
import microservice.inventory_service.internal.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.UserId;

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
            String referenceId
    ) {
        return CreateMovementParams.builder()
                .inventoryId(inventoryId)
                .batchId(batchId)
                .batchId(batchId)
                .quantity(quantity)
                .previousQuantity(previousQuantity)
                .newQuantity(newQuantity)
                .referenceId(referenceId)
                .build();
    }
}
