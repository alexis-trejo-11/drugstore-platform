package microservice.stock.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microservice.inventory.domain.entity.enums.ReservationStatus;
import microservice.inventory.domain.entity.valueobject.id.InventoryID;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservation {
    private ReservationID id;
    private InventoryID inventoryId;
    private String orderId;
    private Integer quantity;
    private ReservationStatus status;
    private LocalDateTime expirationTime;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
