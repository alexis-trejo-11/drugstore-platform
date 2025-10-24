package microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrderId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.core.stock.application.command.ReserveStockCommand;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveStockRequest {
    @NotBlank @NotNull
    private String orderId;
    
    @NotNull
    @Min(value = 1)
    private Integer quantity;
    
    @NotNull
    @Min(value = 1)
    @Max(value = 1440, message = "Duration cannot exceed 24 hours")
    private Integer reservationDurationMinutes;
    
    private String reason;
    
    public ReserveStockCommand toCommand(String inventoryId) {
        return ReserveStockCommand.builder()
            .inventoryId(InventoryId.of(inventoryId))
            .orderId(PurchaseOrderId.of(orderId))
            .quantity(quantity)
            .reservationDurationMinutes(reservationDurationMinutes)
            .reason(reason)
            .build();
    }
}