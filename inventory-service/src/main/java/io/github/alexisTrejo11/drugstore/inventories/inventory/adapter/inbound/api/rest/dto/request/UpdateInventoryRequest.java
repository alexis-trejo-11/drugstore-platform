package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.inbound.api.rest.dto.request;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command.UpdateInventoryCommand;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record UpdateInventoryRequest(
        @Positive Integer reorderLevel,
        @Positive Integer reorderQuantity,
        @Positive Integer maximumStockLevel,
        @Length(max = 255) String warehouseLocation
) {
    public UpdateInventoryCommand toCommand(String inventoryId) {
        return new UpdateInventoryCommand(
                new InventoryId(inventoryId),
                reorderLevel,
                reorderQuantity,
                maximumStockLevel,
                warehouseLocation
        );
    }
}
