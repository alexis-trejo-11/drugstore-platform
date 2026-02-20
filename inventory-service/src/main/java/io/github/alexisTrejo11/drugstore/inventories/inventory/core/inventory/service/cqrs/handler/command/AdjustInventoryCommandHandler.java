package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.command;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command.AdjustInventoryCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port.InventoryRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.InventoryMovement;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.port.InventoryMovementRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.valueobject.MovementType;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.valueobject.CreateMovementParams;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.InventoryNotFoundException;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.port.output.InventoryBatchRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.StockAdjustment;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.entity.StockAdjustmentFactory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.port.output.StockAdjustmentRepository;
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
        registerMovement(savedAdjustment, inventory, quantityBefore, command);
        return savedAdjustment.getId();
    }

    private void registerMovement(StockAdjustment savedAdjustment, Inventory inventory, int quantityBefore, AdjustInventoryCommand command) {
        var params = CreateMovementParams.builder()
                .inventoryId(inventory.getId())
                .batchId(command.batchId())
                .movementType(MovementType.ADJUSTMENT)
                .quantity(Math.abs(command.quantityAdjustment()))
                .previousQuantity(quantityBefore)
                .newQuantity(inventory.getTotalQuantity())
                .reason(command.reason().name())
                .referenceId(savedAdjustment.getId().value())
                .referenceType("ADJUSTMENT")
                .performedBy(command.performedBy())
                .notes(command.notes())
                .build();
        InventoryMovement movement = InventoryMovement.create(params);

        movementRepository.save(movement);
        inventory.recordMovement(movement);
    }
}
