package io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.handler;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.command.MarkBatchAsExpiredCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port.InventoryRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.InventoryMovement;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.valueobject.MovementType;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.valueobject.CreateMovementParams;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.exception.BatchNotFoundException;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.InventoryNotFoundException;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.port.output.InventoryBatchRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.port.InventoryMovementRepository;
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
