package microservice.inventory.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory.application.command.MarkBatchAsExpiredCommand;
import microservice.inventory.domain.entity.Inventory;
import microservice.inventory.domain.entity.InventoryBatch;
import microservice.inventory.domain.entity.InventoryMovement;
import microservice.inventory.domain.entity.enums.MovementType;
import microservice.inventory.domain.exception.BatchNotFoundException;
import microservice.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory.domain.port.output.InventoryBatchRepository;
import microservice.inventory.domain.port.output.InventoryMovementRepository;
import microservice.inventory.domain.port.output.InventoryRepository;
import microservice.inventory.domain.service.BatchManagementService;
import microservice.inventory.domain.service.InventoryStatusService;
import microservice.inventory.factory.InventoryMovementFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MarkBatchAsExpiredCommandHandler {
    private final InventoryBatchRepository batchRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;
    private final BatchManagementService batchManagementService;
    private final InventoryStatusService statusService;

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
