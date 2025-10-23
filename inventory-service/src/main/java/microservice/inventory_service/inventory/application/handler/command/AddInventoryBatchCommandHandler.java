package microservice.inventory_service.inventory.application.handler.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.inventory.application.command.AddInventoryBatchCommand;
import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.domain.entity.InventoryMovement;
import microservice.inventory_service.inventory.domain.entity.valueobject.CreateBatchParams;
import microservice.inventory_service.inventory.domain.entity.valueobject.CreateMovementParams;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory_service.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory_service.inventory.domain.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.domain.port.output.InventoryMovementRepository;
import microservice.inventory_service.inventory.domain.port.output.InventoryRepository;
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
    public BatchId handle(AddInventoryBatchCommand command) {
        log.info("Handling AddInventoryBatchCommand for Inventory ID: {}", command.inventoryId());
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