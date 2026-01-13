package microservice.inventory_service.inventory.core.stock.domain.entity;

import lombok.Getter;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationId;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationStatus;
import microservice.inventory_service.shared.domain.order.BaseDomainEntity;

import java.time.LocalDateTime;

@Getter
public class StockReservationItem extends BaseDomainEntity<ReservationId> {
    private final InventoryId inventoryId;
    private final BatchId associatedBatchId;
    private final Integer quantity;
    private final String reason;
    private ReservationStatus status;

    private StockReservationItem(ReservationId id, InventoryId inventoryId, Integer quantity,
                                 BatchId associatedBatchId,
                                 ReservationStatus status, String reason,
                                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.inventoryId = inventoryId;
        this.associatedBatchId = associatedBatchId;
        this.status = status;
        this.reason = reason;
        this.quantity = quantity;
    }

    public static StockReservationItem create(InventoryId inventoryId, BatchId associatedBatchId, Integer quantity, String reason) {
        return new StockReservationItem(
                ReservationId.generate(),
                inventoryId,
                quantity,
                associatedBatchId,
                ReservationStatus.ACTIVE,
                reason,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public void confirm() {
        if (this.status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE reservations can be confirmed.");
        }
        this.status = ReservationStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    public void release() {
        if (this.status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE reservations can be released.");
        }
        this.status = ReservationStatus.RELEASED;
        this.updatedAt = LocalDateTime.now();
    }


    public static Reconstructor reconstructor() {
        return new Reconstructor();
    }

    public static class Reconstructor {
        private ReservationId id;
        private InventoryId inventoryId;
        private Integer quantity;
        private ReservationStatus status;
        private String reason;
        private BatchId associatedBatchId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Reconstructor id(ReservationId id) {
            this.id = id;
            return this;
        }

        public Reconstructor inventoryId(InventoryId inventoryId) {
            this.inventoryId = inventoryId;
            return this;
        }

        public Reconstructor associatedBatchId(BatchId associatedBatchId) {
            this.associatedBatchId = associatedBatchId;
            return this;
        }


        public Reconstructor quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Reconstructor status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public Reconstructor reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Reconstructor createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Reconstructor updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public StockReservationItem reconstruct() {
            return new StockReservationItem(id, inventoryId, quantity, associatedBatchId, status, reason, createdAt, updatedAt);
        }
    }
}

