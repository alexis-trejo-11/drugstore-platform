package microservice.inventory_service.internal.inventory.adapter.inbound.api.rest.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import microservice.inventory_service.internal.inventory.core.inventory.application.cqrs.command.TransferInventoryCommand;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.UserId;

public record TransferInventoryRequest(
    @NotBlank(message = "Destination inventory ID is required")
    String destinationInventoryId,
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be positive")
    Integer quantity,
    
    @NotBlank(message = "Reason is required")
    String reason,
    
    @NotBlank(message = "Performed by is required")
    String performedBy
) {
    public TransferInventoryCommand toCommand(String sourceInventoryId) {
        return TransferInventoryCommand.builder()
            .sourceInventoryId(InventoryId.of(sourceInventoryId))
            .destinationInventoryId(InventoryId.of(destinationInventoryId))
            .quantity(quantity)
            .reason(reason)
            .performedBy(UserId.of(performedBy))
            .build();
    }
}