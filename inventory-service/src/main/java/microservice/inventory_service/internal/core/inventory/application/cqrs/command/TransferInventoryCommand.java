package microservice.inventory_service.internal.core.inventory.application.cqrs.command;

import lombok.Builder;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;

@Builder
public record TransferInventoryCommand(
        InventoryId sourceInventoryId,
        InventoryId destinationInventoryId,
        Integer quantity,
        String reason,
        UserId performedBy
) {
}
