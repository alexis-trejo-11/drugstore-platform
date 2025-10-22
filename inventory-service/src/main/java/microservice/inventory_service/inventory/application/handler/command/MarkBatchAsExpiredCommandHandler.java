package microservice.inventory_service.inventory.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.application.command.MarkBatchAsExpiredCommand;
import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.domain.entity.InventoryMovement;
import microservice.inventory_service.inventory.domain.entity.enums.MovementType;
import microservice.inventory_service.inventory.domain.exception.BatchNotFoundException;
import microservice.inventory_service.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory_service.inventory.domain.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.domain.port.output.InventoryMovementRepository;
import microservice.inventory_service.inventory.domain.port.output.InventoryRepository;
import microservice.inventory_service.inventory.factory.InventoryMovementFactory;
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

        InventoryMovement movement = InventoryMovementFactory.create(
                inventory.getId(),
                batch.getId(),
                MovementType.EXPIRATION,
                expiredQuantity,
                inventory.getTotalQuantity() + expiredQuantity,
                inventory.getTotalQuantity(),
                "Batch expired",
                batch.getId().value(),
                "BATCH",
                command.performedBy(),
                null
        );

        movementRepository.save(movement);
        inventory.recordMovement(movement);
    }
}
