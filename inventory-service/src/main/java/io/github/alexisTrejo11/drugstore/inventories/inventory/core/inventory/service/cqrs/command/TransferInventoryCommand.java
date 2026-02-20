package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;

@Builder
public record TransferInventoryCommand(
        InventoryId sourceInventoryId,
        InventoryId destinationInventoryId,
        Integer quantity,
        String reason,
        UserId performedBy
) {
}
