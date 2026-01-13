package microservice.inventory_service.inventory.adapter.inbound.api.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationStatus;

@Builder
@Schema(description = "Response DTO for Stock Reservation Item")
public record StockReservationItemResponse(
        @Schema(description = "Unique identifier for the inventory", example = "inv_123456")
        String inventoryId,

        @Schema(description = "Unique identifier for the associated batch", example = "batch_654321")
        String associatedBatchId,

        @Schema(description = "Quantity of stock reserved", example = "10")
        Integer quantity,

        @Schema(description = "Reason for the reservation", example = "Reserved for sale-order")
        String reason,

        @Schema(description = "Current status of the reservation item", example = "ACTIVE, CONFIRMED, RELEASED")
        ReservationStatus status
) {}
