package microservice.inventory_service.inventory.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;

@Builder
public record TransferInventoryCommand(
        InventoryId sourceInventoryId,
        InventoryId destinationInventoryId,
        Integer quantity,
        String reason,
        UserId performedBy
) {
}
