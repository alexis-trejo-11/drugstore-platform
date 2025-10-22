package microservice.inventory_service.inventory.factory;

import microservice.inventory_service.inventory.domain.entity.InventoryMovement;
import microservice.inventory_service.inventory.domain.entity.enums.MovementType;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.MovementId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;

import java.time.LocalDateTime;

public class InventoryMovementFactory {

    public static InventoryMovement create(InventoryId inventoryId, BatchId batchId, MovementType movementType,
                                           Integer quantity, Integer previousQuantity, Integer newQuantity,
                                           String reason, String referenceId, String referenceType,
                                           UserId performedBy, String notes) {
        return InventoryMovement.reconstructor()
            .id(MovementId.generate())
            .inventoryId(inventoryId)
            .batchId(batchId)
            .movementType(movementType)
            .quantity(quantity)
            .previousQuantity(previousQuantity)
            .newQuantity(newQuantity)
            .reason(reason)
            .referenceId(referenceId)
            .referenceType(referenceType)
            .performedBy(performedBy)
            .notes(notes)
            .movementDate(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .reconstruct();
    }
}