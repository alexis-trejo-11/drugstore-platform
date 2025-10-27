package microservice.inventory_service.internal.inventory.core.inventory.application.cqrs.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.inventory.core.inventory.application.cqrs.command.CreateInventoryCommand;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.inventory.core.batch.port.output.InventoryBatchRepository;
import microservice.inventory_service.internal.inventory.core.inventory.port.InventoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateInventoryCommandHandler {
    private final InventoryRepository inventoryRepository;
    private final InventoryBatchRepository batchRepository;

    @Transactional
    public InventoryId handle(CreateInventoryCommand command) {
        if (inventoryRepository.existsByProductId(command.productId())) {
            throw new IllegalStateException("Inventory already exists for this product: " + command.productId());
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
