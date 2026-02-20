package io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.command.RegisterInventoryBatchCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port.InventoryRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.InventoryMovement;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.CreateBatchParams;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.valueobject.CreateMovementParams;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.BatchId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.InventoryNotFoundException;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.port.output.InventoryBatchRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.port.InventoryMovementRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.valueobject.MovementType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AddInventoryBatchCommandHandler {
    private final InventoryRepository inventoryRepository;
    private final InventoryBatchRepository batchRepository;
    private final InventoryMovementRepository movementRepository;

    @Transactional
    public BatchId handle(RegisterInventoryBatchCommand command) {
        log.info("Handling RegisterInventoryBatchCommand for Inventory ID: {}", command.inventoryId());
        Inventory inventory = inventoryRepository.findById(command.inventoryId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        log.info("Creating batch for Inventory ID: {}", command.inventoryId());
        CreateBatchParams params = command.toCreateBatchParams();
        InventoryBatch batch = InventoryBatch.create(params);

        log.info("Saving batch for Inventory ID: {}", command.inventoryId());
        InventoryBatch savedBatch = batchRepository.save(batch);

        log.info("Updating inventory stock for Inventory ID: {}", command.inventoryId());
        inventory.receiveStock(command.quantity());
        inventory.addBatch(savedBatch);

        log.info("Saving updated inventory for Inventory ID: {}", command.inventoryId());
        inventoryRepository.save(inventory);

        // TODO: Publish domain event for batch registration
        log.info("Recording inventory movement for Inventory ID: {}", command.inventoryId());
        log.info("Creating inventory movement for Inventory Quantity: {}", command.quantity());
        var movementParams = CreateMovementParams.batchMovement(
                inventory.getId(),
                savedBatch.getId(),
                command.quantity(),
                inventory.getTotalQuantity() - command.quantity(),
                inventory.getTotalQuantity(),
                savedBatch.getId().value(),
                MovementType.RECEIPT
        );
        InventoryMovement movement = InventoryMovement.create(movementParams);
        inventory.recordMovement(movement);

        log.info("Saving inventory movement for Inventory ID: {}", command.inventoryId());
        movementRepository.save(movement);

        log.info("RegisterInventoryBatchCommand handled successfully for Inventory ID: {}", command.inventoryId());
        return savedBatch.getId();
    }
}