package microservice.inventory_service.internal.core.stock.domain.entity;

import microservice.inventory_service.internal.core.stock.domain.valueobject.ReservationStatus;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.external.order.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.internal.core.stock.domain.valueobject.ReservationId;
import microservice.inventory_service.internal.core.stock.domain.valueobject.StockReservation;

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
