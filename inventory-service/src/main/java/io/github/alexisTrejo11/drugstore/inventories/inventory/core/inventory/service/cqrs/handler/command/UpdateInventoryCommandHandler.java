package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.command;

import libs_kernel.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.InventoryNotFoundException;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port.InventoryRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command.UpdateInventoryCommand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateInventoryCommandHandler {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public void handle(UpdateInventoryCommand command) {
        if (command.reorderLevel() == null && command.reorderQuantity() == null
                && command.maximumStockLevel() == null && command.warehouseLocation() == null) {
            throw new BadRequestException("At least one field must be provided to update inventory settings");
        }

        Inventory inventory = inventoryRepository.findById(command.inventoryId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        inventory.updateSettings(
                command.reorderLevel(),
                command.reorderQuantity(),
                command.maximumStockLevel(),
                command.warehouseLocation()
        );
        inventoryRepository.save(inventory);
    }
}
