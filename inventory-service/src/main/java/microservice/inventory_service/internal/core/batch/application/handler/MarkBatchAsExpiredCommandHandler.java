package microservice.inventory_service.internal.core.batch.application.handler;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.batch.application.command.MarkBatchAsExpiredCommand;
import microservice.inventory_service.internal.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.core.inventory.port.InventoryRepository;
import microservice.inventory_service.internal.core.movement.domain.InventoryMovement;
import microservice.inventory_service.internal.core.movement.domain.valueobject.MovementType;
import microservice.inventory_service.internal.core.movement.domain.valueobject.CreateMovementParams;
import microservice.inventory_service.internal.core.batch.domain.exception.BatchNotFoundException;
import microservice.inventory_service.internal.core.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory_service.internal.core.batch.port.output.InventoryBatchRepository;
import microservice.inventory_service.internal.core.movement.domain.port.InventoryMovementRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MarkBatchAsExpiredCommandHandler {
    private final InventoryBatchRepository batchRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;

    @Transactional
    public void handle(MarkBatchAsExpiredCommand command) {
        InventoryBatch batch = batchRepository.findById(command.batchId())
                .orElseThrow(() -> new BatchNotFoundException("Batch not found"));

        Inventory inventory = inventoryRepository.findById(batch.getInventoryId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        int expiredQuantity = batch.getAvailableQuantity();

        batch.markAsExpired();
        batchRepository.save(batch);

        inventory.decreaseStock(expiredQuantity);
        inventoryRepository.save(inventory);

        var param = CreateMovementParams.builder()
                .inventoryId(inventory.getId())
                .movementType(MovementType.EXPIRATION)
                .quantity(expiredQuantity)
                .previousQuantity(inventory.getTotalQuantity() + expiredQuantity)
                .newQuantity(inventory.getTotalQuantity())
                .reason("Batch expired")
                .referenceId(batch.getId().value())
                .referenceType("BATCH")
                .performedBy(command.performedBy())
                .build();
        InventoryMovement movement = InventoryMovement.create(param);

        movementRepository.save(movement);
        inventory.recordMovement(movement);
    }
}
