package microservice.inventory_service.internal.inventory.core.inventory.application.cqrs.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.inventory.core.inventory.application.cqrs.command.TransferInventoryCommand;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.inventory.core.inventory.port.InventoryRepository;
import microservice.inventory_service.internal.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.internal.inventory.core.movement.domain.port.InventoryMovementRepository;
import microservice.inventory_service.internal.inventory.core.movement.domain.valueobject.MovementType;
import microservice.inventory_service.internal.inventory.core.movement.domain.valueobject.CreateMovementParams;
import microservice.inventory_service.internal.inventory.core.inventory.domain.exception.InventoryNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TransferInventoryCommandHandler {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;

    @Transactional
    public void handle(TransferInventoryCommand command) {
        Inventory sourceInventory = inventoryRepository.findById(command.sourceInventoryId())
                .orElseThrow(() -> new InventoryNotFoundException("Source inventory not found"));

        Inventory destinationInventory = inventoryRepository.findById(command.destinationInventoryId())
                .orElseThrow(() -> new InventoryNotFoundException("Destination inventory not found"));

        sourceInventory.decreaseStock(command.quantity());
        destinationInventory.receiveStock(command.quantity());

        inventoryRepository.save(sourceInventory);
        inventoryRepository.save(destinationInventory);

        InventoryMovement sourceMovement = InventoryMovement.create(
                CreateMovementParams.builder()
                        .inventoryId(sourceInventory.getId())
                        .movementType(MovementType.TRANSFER)
                        .quantity(command.quantity())
                        .previousQuantity(sourceInventory.getTotalQuantity() + command.quantity())
                        .newQuantity(sourceInventory.getTotalQuantity())
                        .reason(command.reason())
                        .referenceId(destinationInventory.getId().value())
                        .referenceType("TRANSFER_OUT")
                        .performedBy(command.performedBy())
                        .build()
        );

        var params = CreateMovementParams.builder()
                .inventoryId(destinationInventory.getId())
                .movementType(MovementType.TRANSFER)
                .quantity(command.quantity())
                .previousQuantity(destinationInventory.getTotalQuantity() - command.quantity())
                .newQuantity(destinationInventory.getTotalQuantity())
                .reason(command.reason())
                .referenceId(sourceInventory.getId().value())
                .referenceType("TRANSFER_IN")
                .performedBy(command.performedBy())
                .build();
        InventoryMovement destinationMovement = InventoryMovement.create(params);

        movementRepository.save(sourceMovement);
        movementRepository.save(destinationMovement);

        sourceInventory.recordMovement(sourceMovement);
        destinationInventory.recordMovement(destinationMovement);
    }
}