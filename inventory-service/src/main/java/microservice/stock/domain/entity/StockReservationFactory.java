package microservice.stock.domain.entity;

import microservice.inventory.domain.entity.enums.ReservationStatus;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.purchase.domain.entity.PurchaseOrderId;
import microservice.stock.domain.ReservationId;
import microservice.stock.domain.StockReservation;

import java.time.LocalDateTime;

public class StockReservationFactory {

    public static StockReservation create(InventoryId inventoryId, PurchaseOrderId orderId, Integer quantity,
                                          Integer durationMinutes, String reason) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }

        return StockReservation.reconstructor()
                .id(ReservationId.generate())
                .inventoryId(inventoryId)
                .orderId(orderId)
                .quantity(quantity)
                .status(ReservationStatus.ACTIVE)
                .expirationTime(LocalDateTime.now().plusMinutes(durationMinutes))
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .reconstruct();
    }
}
