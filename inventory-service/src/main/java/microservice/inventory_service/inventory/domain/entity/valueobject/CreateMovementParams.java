package microservice.inventory_service.inventory.domain.entity.valueobject;

import lombok.Builder;
import microservice.inventory_service.inventory.domain.entity.enums.MovementType;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;

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
