package microservice.inventory.application.command;

import lombok.Builder;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory.domain.entity.valueobject.id.UserId;

@Builder
public record TransferInventoryCommand(
        InventoryId sourceInventoryId,
        InventoryId destinationInventoryId,
        Integer quantity,
        String reason,
        UserId performedBy
) {
}
