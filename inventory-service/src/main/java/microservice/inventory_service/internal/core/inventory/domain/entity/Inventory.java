package microservice.inventory_service.internal.core.inventory.domain.entity;

import lombok.Getter;
import microservice.inventory_service.internal.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.core.inventory.domain.entity.enums.InventoryStatus;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.CreateInventoryParams;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.MedicineId;
import microservice.inventory_service.internal.core.inventory.domain.exception.InsufficientInventoryException;
import microservice.inventory_service.internal.core.inventory.domain.exception.InvalidStockAdjustmentException;
import microservice.inventory_service.internal.core.inventory.domain.exception.InventoryValidationException;
import microservice.inventory_service.internal.core.movement.domain.InventoryMovement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Getter
public class Inventory {
    private InventoryId id;
    private MedicineId medicineId;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer reorderLevel;
    private Integer reorderQuantity;
    private Integer maximumStockLevel;
    private String warehouseLocation;
    private InventoryStatus status;
    private LocalDateTime lastRestockedDate;
    private LocalDateTime lastCountDate;
    private List<InventoryBatch> batches;
    private List<InventoryMovement> movements;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Inventory(InventoryId id, MedicineId medicineId, Integer totalQuantity, Integer availableQuantity, Integer reservedQuantity, Integer reorderLevel, Integer reorderQuantity, Integer maximumStockLevel, String warehouseLocation, InventoryStatus status, LocalDateTime lastRestockedDate, LocalDateTime lastCountDate, List<InventoryBatch> batches, List<InventoryMovement> movements, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.medicineId = medicineId;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
        this.reorderLevel = reorderLevel;
        this.reorderQuantity = reorderQuantity;
        this.maximumStockLevel = maximumStockLevel;
        this.warehouseLocation = warehouseLocation;
        this.status = status;
        this.lastRestockedDate = lastRestockedDate;
        this.lastCountDate = lastCountDate;
        this.batches = batches != null ? new ArrayList<>(batches) : new ArrayList<>();
        this.movements = movements != null ? new ArrayList<>(movements) : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Inventory create(CreateInventoryParams params) {
        params.validate();

        LocalDateTime now = LocalDateTime.now();
        if (params.initialBatch().isPresent()) {
            return createFromInitialBatch(params, params.initialBatch().get(), now);
        }


        return createEmptyInventory(params, now);
    }

    private static Inventory createEmptyInventory(CreateInventoryParams params, LocalDateTime now) {
        return new Inventory(InventoryId.generate(), params.medicineId(), 0, 0, 0, params.reorderLevel(), params.reorderQuantity(), params.maximumStockLevel(), params.warehouseLocation(), determineInitialStatus(0, params.reorderLevel()), now, null, new ArrayList<>(), new ArrayList<>(), now, now);
    }

    private static Inventory createFromInitialBatch(CreateInventoryParams params, InventoryBatch initialBatch, LocalDateTime now) {
        int initialQuantity = initialBatch.getQuantity();
        return new Inventory(InventoryId.generate(), params.medicineId(), initialQuantity, initialQuantity, 0, params.reorderLevel(), params.reorderQuantity(), params.maximumStockLevel(), params.warehouseLocation(), determineInitialStatus(initialQuantity, params.reorderLevel()), now, null, new ArrayList<>(List.of(initialBatch)), new ArrayList<>(), now, now);
    }


    private static InventoryStatus determineInitialStatus(Integer quantity, Integer reorderLevel) {
        if (quantity <= 0) {
            return InventoryStatus.OUT_OF_STOCK;
        } else if (quantity <= reorderLevel) {
            return InventoryStatus.LOW_STOCK;
        } else {
            return InventoryStatus.ACTIVE;
        }
    }

    public void receiveStock(Integer quantity) {
        if (quantity < 0) throw new InsufficientInventoryException("Cannot receive negative stock quantity");

        this.totalQuantity += quantity;
        this.availableQuantity += quantity;
        this.lastRestockedDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    public void reserveStock(Integer quantity) {
        this.availableQuantity -= quantity;
        this.reservedQuantity += quantity;
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    public void releaseReservation(Integer quantity) {
        if (reservedQuantity < quantity) {
            throw new InventoryValidationException("Cannot release more than reserved quantity");
        }

        this.availableQuantity += quantity;
        this.reservedQuantity -= quantity;
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    public void confirmReservation(Integer quantity) {
        if (reservedQuantity < quantity) {
            throw new InventoryValidationException("Cannot confirm more than reserved quantity");
        }

        this.reservedQuantity -= quantity;
        this.totalQuantity -= quantity;
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    public void adjustStock(Integer adjustment) {
        int newTotal = this.totalQuantity + adjustment;
        if (newTotal < 0) {
            throw new InvalidStockAdjustmentException("Adjustment would result in negative stock");
        }

        this.totalQuantity = newTotal;
        this.availableQuantity += adjustment;
        this.updatedAt = LocalDateTime.now();
        updateStatus();

    }

    public void decreaseStock(Integer quantity) {
        if (this.totalQuantity < quantity) {
            throw new InsufficientInventoryException("Cannot decrease stock below zero");
        }
        if (this.availableQuantity < quantity) {
            throw new InsufficientInventoryException("Cannot decrease available stock below zero");
        }
        this.totalQuantity -= quantity;
        this.availableQuantity -= quantity;
        this.updatedAt = LocalDateTime.now();
        updateStatus();

    }

    public void addBatch(InventoryBatch batch) {
        this.batches.add(batch);
    }

    public void recordMovement(InventoryMovement movement) {
        this.movements.add(movement);
    }

    public List<InventoryBatch> getBatches() {
        return Collections.unmodifiableList(batches);
    }

    public List<InventoryMovement> getMovements() {
        return Collections.unmodifiableList(movements);
    }

    public boolean isLowStock() {
        return this.availableQuantity <= this.reorderLevel;
    }

    public boolean isOutOfStock() {
        return this.availableQuantity <= 0;
    }

    public boolean needsReorder() {
        return this.availableQuantity <= this.reorderLevel;
    }

    public Integer calculateReorderQuantity() {
        return this.maximumStockLevel - this.totalQuantity;
    }

    private void validateSufficientStock(Integer quantity) {
        if (this.availableQuantity < quantity) {
            throw new InsufficientInventoryException(String.format("Insufficient inventory. Available: %d, Requested: %d", this.availableQuantity, quantity));
        }
    }

    public void updateStockCount(Integer countedQuantity) {
        this.totalQuantity = countedQuantity;
        this.availableQuantity = countedQuantity - this.reservedQuantity;
        this.lastCountDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }


    public void updateStatus() {
        if (this.availableQuantity <= 0) {
            this.status = InventoryStatus.OUT_OF_STOCK;
        } else if (this.availableQuantity <= this.reorderLevel) {
            this.status = InventoryStatus.LOW_STOCK;
        } else {
            this.status = InventoryStatus.ACTIVE;
        }
    }

    public static InventoryReconstructor reconstructor() {
        return new InventoryReconstructor();
    }

    public static class InventoryReconstructor {
        private InventoryId id;
        private MedicineId medicineId;
        private Integer totalQuantity;
        private Integer availableQuantity;
        private Integer reservedQuantity;
        private Integer reorderLevel;
        private Integer reorderQuantity;
        private Integer maximumStockLevel;
        private String warehouseLocation;
        private InventoryStatus status;
        private LocalDateTime lastRestockedDate;
        private LocalDateTime lastCountDate;
        private List<InventoryBatch> batches;
        private List<InventoryMovement> movements;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public InventoryReconstructor id(InventoryId id) {
            this.id = id;
            return this;
        }

        public InventoryReconstructor medicineId(MedicineId medicineId) {
            this.medicineId = medicineId;
            return this;
        }

        public InventoryReconstructor totalQuantity(Integer totalQuantity) {
            this.totalQuantity = totalQuantity;
            return this;
        }

        public InventoryReconstructor availableQuantity(Integer availableQuantity) {
            this.availableQuantity = availableQuantity;
            return this;
        }

        public InventoryReconstructor reservedQuantity(Integer reservedQuantity) {
            this.reservedQuantity = reservedQuantity;
            return this;
        }

        public InventoryReconstructor reorderLevel(Integer reorderLevel) {
            this.reorderLevel = reorderLevel;
            return this;
        }

        public InventoryReconstructor reorderQuantity(Integer reorderQuantity) {
            this.reorderQuantity = reorderQuantity;
            return this;
        }

        public InventoryReconstructor maximumStockLevel(Integer maximumStockLevel) {
            this.maximumStockLevel = maximumStockLevel;
            return this;
        }

        public InventoryReconstructor warehouseLocation(String warehouseLocation) {
            this.warehouseLocation = warehouseLocation;
            return this;
        }

        public InventoryReconstructor status(InventoryStatus status) {
            this.status = status;
            return this;
        }

        public InventoryReconstructor lastRestockedDate(LocalDateTime lastRestockedDate) {
            this.lastRestockedDate = lastRestockedDate;
            return this;
        }

        public InventoryReconstructor lastCountDate(LocalDateTime lastCountDate) {
            this.lastCountDate = lastCountDate;
            return this;
        }

        public InventoryReconstructor batches(List<InventoryBatch> batches) {
            this.batches = batches;
            return this;
        }

        public InventoryReconstructor movements(List<InventoryMovement> movements) {
            this.movements = movements;
            return this;
        }

        public InventoryReconstructor createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public InventoryReconstructor updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Inventory reconstruct() {
            return new Inventory(id, medicineId, totalQuantity, availableQuantity, reservedQuantity, reorderLevel, reorderQuantity, maximumStockLevel, warehouseLocation, status, lastRestockedDate, lastCountDate, batches, movements, createdAt, updatedAt);
        }
    }
}
