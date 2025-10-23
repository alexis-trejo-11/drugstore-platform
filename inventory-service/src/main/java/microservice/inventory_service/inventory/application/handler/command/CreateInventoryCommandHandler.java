package microservice.inventory_service.inventory.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.application.command.CreateInventoryCommand;
import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.domain.port.output.InventoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateInventoryCommandHandler {
    private final InventoryRepository inventoryRepository;
    private final InventoryBatchRepository batchRepository;

    @Transactional
    public InventoryId handle(CreateInventoryCommand command) {
        if (inventoryRepository.existsByMedicineId(command.medicineId())) {
            throw new IllegalStateException("Inventory already exists for this medicine");
        }

        var createParams = command.toCreateInventoryParams();
        Inventory inventory = Inventory.create(createParams);
        Inventory savedInventory = inventoryRepository.save(inventory);

        if (command.hasBatch()) {
            var batch = savedInventory.getBatches().getFirst();
            batchRepository.save(batch);
        }

        return savedInventory.getId();
    }
}
