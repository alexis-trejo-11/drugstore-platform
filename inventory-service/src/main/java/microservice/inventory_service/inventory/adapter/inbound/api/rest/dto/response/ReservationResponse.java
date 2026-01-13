package microservice.inventory_service.inventory.adapter.inbound.api.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationStatus;
import microservice.inventory_service.shared.domain.order.OrderReference;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response DTO for Stock Reservation")
public record ReservationResponse(
        @Schema(description = "Unique identifier for the reservation", example = "resv_123456")
        String reservationId,

        @Schema(description = "Expiration time of the reservation", example = "2024-12-31T23:59:59")
        LocalDateTime expirationTime,

        @Schema(description = "Order ID associated with the reservation", example = "order_78910")
        String orderId,

        @Schema(description = "Type of the order associated with the reservation", example = "ONLINE, IN_STORE")
        OrderReference.OrderType orderType,

        @Schema(description = "List of stock reservation items")
        List<StockReservationItemResponse> reservations,

        @Schema(description = "Current status of the reservation", example = "ACTIVE, CONFIRMED, RELEASED")
        ReservationStatus status,

        @Schema(description = "Timestamp when the reservation was created", example = "2024-01-01T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when the reservation was last updated", example = "2024-01-15T12:00:00")
        LocalDateTime updatedAt
) {}
