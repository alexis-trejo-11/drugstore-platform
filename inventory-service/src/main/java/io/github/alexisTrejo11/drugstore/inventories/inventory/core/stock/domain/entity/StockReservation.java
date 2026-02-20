package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.entity;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.BatchId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.ReservationId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.ReservationStatus;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.BaseDomainEntity;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderReference;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockReservation extends BaseDomainEntity<ReservationId> {
    private static final long DEFAULT_EXPIRATION_DAYS = 30; // 1 month
    private final LocalDateTime expirationTime;
    private final OrderReference orderReference;
    private Map<InventoryId, StockReservationItem> reservations;
    private ReservationStatus status;

    public StockReservation(ReservationId id, OrderReference orderReference, Map<InventoryId, StockReservationItem> reservations, ReservationStatus status, LocalDateTime expirationTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.orderReference = orderReference;
        this.reservations = reservations;
        this.expirationTime = expirationTime;
        this.status = status;
    }

    private StockReservation(Reconstructor reconstructor) {
        super(reconstructor.id, reconstructor.createdAt, reconstructor.updatedAt);
        this.orderReference = reconstructor.orderReference;
        this.reservations = reconstructor.reservations;
        this.status = reconstructor.status;
        this.expirationTime = reconstructor.expirationTime;
    }

    public boolean isActive() {
        return this.status == ReservationStatus.ACTIVE;
    }

    public static StockReservation create(OrderReference orderReference) {
        return new StockReservation(ReservationId.generate(), orderReference, new HashMap<>(), ReservationStatus.ACTIVE, LocalDateTime.now().plusDays(DEFAULT_EXPIRATION_DAYS), LocalDateTime.now(), LocalDateTime.now());
    }

    public void addStockReservation(StockReservationItem item) {
        this.reservations.put(item.getInventoryId(), item);
        this.updatedAt = LocalDateTime.now();
    }

    public void confirmAll() {
        this.reservations.values().forEach(StockReservationItem::confirm);
        this.status = ReservationStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    public void releaseAll() {
        this.reservations.values().forEach(StockReservationItem::release);
        this.status = ReservationStatus.RELEASED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasReservationFor(InventoryId inventoryId) {
        return this.reservations.containsKey(inventoryId);
    }

    public Integer getReservedQuantityFor(InventoryId inventoryId) {
        return this.reservations.get(inventoryId).getQuantity();
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public OrderReference getOrderReference() {
        return orderReference;
    }

    public Map<InventoryId, StockReservationItem> getReservations() {
        return reservations;
    }

    public BatchId getAssociatedBatchIdFor(InventoryId inventoryId) {
        return this.reservations.get(inventoryId).getAssociatedBatchId();
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public List<InventoryId> getInventoryIds() {
        if (this.reservations.isEmpty()) {
            return List.of();
        }

        return this.reservations.values()
                .stream()
                .map(StockReservationItem::getInventoryId)
                .toList();
    }

    public static Reconstructor reconstructor() {
        return new Reconstructor();
    }

    public static class Reconstructor {
        private ReservationId id;
        private OrderReference orderReference;
        private Map<InventoryId, StockReservationItem> reservations;
        private ReservationStatus status;
        private LocalDateTime expirationTime;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Reconstructor id(ReservationId id) {
            this.id = id;
            return this;
        }

        public Reconstructor orderReference(OrderReference orderReference) {
            this.orderReference = orderReference;
            return this;
        }

        public Reconstructor reservations(Map<InventoryId, StockReservationItem> reservations) {
            this.reservations = reservations;
            return this;
        }

        public Reconstructor status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public Reconstructor expirationTime(LocalDateTime expirationTime) {
            this.expirationTime = expirationTime;
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

        public StockReservation reconstruct() {
            return new StockReservation(this);
        }
    }
}
