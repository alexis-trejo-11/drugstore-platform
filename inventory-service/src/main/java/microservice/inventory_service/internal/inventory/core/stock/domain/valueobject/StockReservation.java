package microservice.inventory_service.internal.inventory.core.stock.domain.valueobject;

import lombok.Getter;
import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.inventory.core.inventory.domain.exception.ReservationExpiredException;

import java.time.LocalDateTime;

@Getter
public class StockReservation {
    private ReservationId id;
    private InventoryId inventoryId;
    private PurchaseOrderId purchaseOrderId;
    private Integer quantity;
    private ReservationStatus status;
    private LocalDateTime expirationTime;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private StockReservation(ReservationId id, InventoryId inventoryId, PurchaseOrderId purchaseOrderId, Integer quantity,
                             ReservationStatus status, LocalDateTime expirationTime, String reason,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.purchaseOrderId = purchaseOrderId;
        this.quantity = quantity;
        this.status = status;
        this.expirationTime = expirationTime;
        this.reason = reason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void validateActive() {
        if (this.status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Reservation is not active");
        }
        if (isExpired()) {
            throw new ReservationExpiredException("Reservation has expired");
        }
    }

    public void confirm() {
        validateActive();
        this.status = ReservationStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    public void release() {
        if (this.status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Can only release active reservations");
        }
        this.status = ReservationStatus.RELEASED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsExpired() {
        this.status = ReservationStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationTime);
    }

    public boolean isActive() {
        return this.status == ReservationStatus.ACTIVE && !isExpired();
    }

    public static StockReservationReconstructor reconstructor() {
        return new StockReservationReconstructor();
    }

    public static class StockReservationReconstructor {
        private ReservationId id;
        private InventoryId inventoryId;
        private PurchaseOrderId purchaseOrderId;
        private Integer quantity;
        private ReservationStatus status;
        private LocalDateTime expirationTime;
        private String reason;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public StockReservationReconstructor id(ReservationId id) {
            this.id = id;
            return this;
        }

        public StockReservationReconstructor inventoryId(InventoryId inventoryId) {
            this.inventoryId = inventoryId;
            return this;
        }

        public StockReservationReconstructor orderId(PurchaseOrderId purchaseOrderId) {
            this.purchaseOrderId = purchaseOrderId;
            return this;
        }

        public StockReservationReconstructor quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public StockReservationReconstructor status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public StockReservationReconstructor expirationTime(LocalDateTime expirationTime) {
            this.expirationTime = expirationTime;
            return this;
        }

        public StockReservationReconstructor reason(String reason) {
            this.reason = reason;
            return this;
        }

        public StockReservationReconstructor createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public StockReservationReconstructor updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public StockReservation reconstruct() {
            return new StockReservation(id, inventoryId, purchaseOrderId, quantity, status,
                    expirationTime, reason, createdAt, updatedAt);
        }
    }
}

