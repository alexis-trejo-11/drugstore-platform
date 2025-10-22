package microservice.stock.domain;

import lombok.Getter;
import microservice.inventory.domain.entity.enums.ReservationStatus;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory.domain.exception.ReservationExpiredException;
import microservice.purchase.domain.entity.PurchaseOrderId;

import java.time.LocalDateTime;

@Getter
public class StockReservation {
    private ReservationId id;
    private InventoryId inventoryId;
    private PurchaseOrderId orderId;
    private Integer quantity;
    private ReservationStatus status;
    private LocalDateTime expirationTime;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private StockReservation(ReservationId id, InventoryId inventoryId, PurchaseOrderId orderId, Integer quantity, ReservationStatus status, LocalDateTime expirationTime, String reason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.orderId = orderId;
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

    public void release() {
        if (this.status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Can only release active reservations");
        }

        this.status = ReservationStatus.RELEASED;
        this.updatedAt = LocalDateTime.now();
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
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

    public boolean isActive() {
        return this.status == ReservationStatus.ACTIVE && !isExpired();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationTime);
    }
}

