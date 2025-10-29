package microservice.inventory_service.inventory.core.batch.application.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.inventory.core.batch.application.command.AddInventoryBatchCommand;
import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.inventory.port.InventoryRepository;
import microservice.inventory_service.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.CreateBatchParams;
import microservice.inventory_service.inventory.core.movement.domain.valueobject.CreateMovementParams;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory_service.inventory.core.batch.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.core.movement.domain.port.InventoryMovementRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AddInventoryBatchCommandHandler {
    private final InventoryRepository alertRepository;
    private final InventoryBatchRepository batchRepository;
    private final InventoryMovementRepository movementRepository;

    @Transactional
    public BatchId handle(AddInventoryBatchCommand command) {
        log.info("Handling AddInventoryBatchCommand for Inventory ID: {}", command.inventoryId());
        Inventory inventory = alertRepository.findById(command.inventoryId())
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
        alertRepository.save(inventory);

        log.info("Recording inventory movement for Inventory ID: {}", command.inventoryId());
        var movementParams = CreateMovementParams.batchMovement(
                inventory.getId(),
                savedBatch.getId(),
                command.quantity(),
                inventory.getTotalQuantity() - command.quantity(),
                inventory.getTotalQuantity(),
                savedBatch.getId().value()
        );
        InventoryMovement movement = InventoryMovement.create(movementParams);
        inventory.recordMovement(movement);

        log.info("Saving inventory movement for Inventory ID: {}", command.inventoryId());
        movementRepository.save(movement);

        log.info("AddInventoryBatchCommand handled successfully for Inventory ID: {}", command.inventoryId());
        return savedBatch.getId();
    }
}