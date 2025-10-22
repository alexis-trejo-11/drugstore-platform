package microservice.inventory_service.inventory.application.handler.command;


import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.application.command.AdjustInventoryCommand;
import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.domain.entity.InventoryMovement;
import microservice.inventory_service.inventory.domain.entity.enums.MovementType;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.AdjustmentId;
import microservice.inventory_service.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory_service.inventory.domain.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.domain.port.output.InventoryMovementRepository;
import microservice.inventory_service.inventory.domain.port.output.InventoryRepository;
import microservice.inventory_service.inventory.factory.InventoryMovementFactory;
import microservice.inventory_service.stock.domain.StockAdjustment;
import microservice.inventory_service.stock.domain.entity.StockAdjustmentFactory;
import microservice.inventory_service.stock.domain.port.output.StockAdjustmentRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdjustInventoryCommandHandler {

    private final InventoryRepository inventoryRepository;
    private final InventoryBatchRepository batchRepository;
    private final StockAdjustmentRepository adjustmentRepository;
    private final InventoryMovementRepository movementRepository;

    @Transactional
    public AdjustmentId handle(AdjustInventoryCommand command) {
        Inventory inventory = inventoryRepository.findById(command.inventoryId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        int quantityBefore = inventory.getTotalQuantity();

        InventoryBatch batch = null;
        if (command.batchId() != null) {
            batch = batchRepository.findById(command.batchId())
                    .orElseThrow(() -> new IllegalStateException("Batch not found"));
        }

        inventory.adjustStock(command.quantityAdjustment());
        inventoryRepository.save(inventory);

        if (batch != null) {
            if (command.quantityAdjustment() > 0) {
                batch.releaseQuantity(command.quantityAdjustment());
            } else {
                batch.allocateQuantity(Math.abs(command.quantityAdjustment()));
            }
            batchRepository.save(batch);
        }

        StockAdjustment adjustment = StockAdjustmentFactory.create(
                command.inventoryId(),
                command.batchId(),
                quantityBefore,
                command.quantityAdjustment(),
                command.reason(),
                command.notes(),
                command.approvedBy(),
                command.performedBy()
        );

        StockAdjustment savedAdjustment = adjustmentRepository.save(adjustment);

        InventoryMovement movement = InventoryMovementFactory.create(
                inventory.getId(),
                command.batchId(),
                MovementType.ADJUSTMENT,
                Math.abs(command.quantityAdjustment()),
                quantityBefore,
                inventory.getTotalQuantity(),
                command.reason().name(),
                savedAdjustment.getId().value(),
                "ADJUSTMENT",
                command.performedBy(),
                command.notes()
        );

        movementRepository.save(movement);
        inventory.recordMovement(movement);

        return savedAdjustment.getId();
    }
}
