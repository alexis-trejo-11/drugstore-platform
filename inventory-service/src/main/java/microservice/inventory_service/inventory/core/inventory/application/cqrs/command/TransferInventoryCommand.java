package microservice.inventory_service.inventory.core.inventory.application.cqrs.command;

import lombok.Builder;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;

@Builder
public record TransferInventoryCommand(
        InventoryId sourceInventoryId,
        InventoryId destinationInventoryId,
        Integer quantity,
        String reason,
        UserId performedBy
) {
}
