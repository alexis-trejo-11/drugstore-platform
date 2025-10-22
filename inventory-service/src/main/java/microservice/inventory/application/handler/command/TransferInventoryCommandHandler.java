package microservice.inventory.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory.application.command.TransferInventoryCommand;
import microservice.inventory.domain.entity.Inventory;
import microservice.inventory.domain.entity.InventoryMovement;
import microservice.inventory.domain.entity.enums.MovementType;
import microservice.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory.domain.port.output.InventoryMovementRepository;
import microservice.inventory.domain.port.output.InventoryRepository;
import microservice.inventory.factory.InventoryMovementFactory;
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

        InventoryMovement sourceMovement = InventoryMovementFactory.create(
                sourceInventory.getId(),
                null,
                MovementType.TRANSFER,
                command.quantity(),
                sourceInventory.getTotalQuantity() + command.quantity(),
                sourceInventory.getTotalQuantity(),
                command.reason(),
                destinationInventory.getId().value(),
                "TRANSFER_OUT",
                command.performedBy(),
                null
        );

        InventoryMovement destinationMovement = InventoryMovementFactory.create(
                destinationInventory.getId(),
                null,
                MovementType.TRANSFER,
                command.quantity(),
                destinationInventory.getTotalQuantity() - command.quantity(),
                destinationInventory.getTotalQuantity(),
                command.reason(),
                sourceInventory.getId().value(),
                "TRANSFER_IN",
                command.performedBy(),
                null
        );

        movementRepository.save(sourceMovement);
        movementRepository.save(destinationMovement);

        sourceInventory.recordMovement(sourceMovement);
        destinationInventory.recordMovement(destinationMovement);
    }
}