package microservice.inventory.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory.application.command.AddInventoryBatchCommand;
import microservice.inventory.domain.entity.Inventory;
import microservice.inventory.domain.entity.InventoryBatch;
import microservice.inventory.domain.entity.InventoryMovement;
import microservice.inventory.domain.entity.enums.MovementType;
import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory.domain.port.output.InventoryBatchRepository;
import microservice.inventory.domain.port.output.InventoryMovementRepository;
import microservice.inventory.domain.port.output.InventoryRepository;
import microservice.inventory.factory.InventoryBatchFactory;
import microservice.inventory.factory.InventoryMovementFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AddInventoryBatchCommandHandler {

    private final InventoryRepository inventoryRepository;
    private final InventoryBatchRepository batchRepository;
    private final InventoryMovementRepository movementRepository;

    @Transactional
    public BatchId handle(AddInventoryBatchCommand command) {
        Inventory inventory = inventoryRepository.findById(command.inventoryId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        InventoryBatch batch = InventoryBatchFactory.create(
                command.inventoryId(),
                command.batchNumber(),
                command.lotNumber(),
                command.quantity(),
                command.costPerUnit(),
                command.manufacturingDate(),
                command.expirationDate(),
                command.supplierId(),
                command.supplierName(),
                command.storageConditions()
        );

        InventoryBatch savedBatch = batchRepository.save(batch);

        inventory.receiveStock(command.quantity());
        inventory.addBatch(savedBatch);
        inventoryRepository.save(inventory);

        InventoryMovement movement = InventoryMovementFactory.create(
                inventory.getId(),
                savedBatch.getId(),
                MovementType.RECEIPT,
                command.quantity(),
                inventory.getTotalQuantity() - command.quantity(),
                inventory.getTotalQuantity(),
                "Batch received",
                savedBatch.getId().value(),
                "BATCH",
                null,
                null
        );

        movementRepository.save(movement);
        inventory.recordMovement(movement);

        return savedBatch.getId();
    }
}