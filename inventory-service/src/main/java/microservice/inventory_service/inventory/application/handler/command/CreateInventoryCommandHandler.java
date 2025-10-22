package microservice.inventory_service.inventory.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.application.command.CreateInventoryCommand;
import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.port.output.InventoryRepository;
import microservice.inventory_service.inventory.factory.InventoryFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateInventoryCommandHandler {
    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryId handle(CreateInventoryCommand command) {
        if (inventoryRepository.existsByMedicineId(command.medicineId())) {
            throw new IllegalStateException("Inventory already exists for this medicine");
        }

        Inventory inventory = InventoryFactory.create(
                command.medicineId(),
                command.initialQuantity(),
                command.reorderLevel(),
                command.reorderQuantity(),
                command.maximumStockLevel(),
                command.warehouseLocation()
        );

        Inventory savedInventory = inventoryRepository.save(inventory);
        return savedInventory.getId();
    }
}
