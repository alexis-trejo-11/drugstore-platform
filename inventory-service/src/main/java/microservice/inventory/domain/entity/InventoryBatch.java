package microservice.inventory.domain.entity;

import lombok.Getter;
import microservice.inventory.domain.entity.enums.BatchStatus;
import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory.domain.exception.BatchExpiredException;
import microservice.inventory.domain.exception.InvalidBatchException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
public class InventoryBatch {
    private BatchId id;
    private InventoryId inventoryId;
    private String batchNumber;
    private String lotNumber;
    private Integer quantity;
    private Integer availableQuantity;
    private BigDecimal costPerUnit;
    private LocalDateTime manufacturingDate;
    private LocalDateTime expirationDate;
    private String supplierId;
    private String supplierName;
    private BatchStatus status;
    private String storageConditions;
    private LocalDateTime receivedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private InventoryBatch(BatchId id, InventoryId inventoryId, String batchNumber, String lotNumber, Integer quantity, Integer availableQuantity, BigDecimal costPerUnit, LocalDateTime manufacturingDate, LocalDateTime expirationDate, String supplierId, String supplierName, BatchStatus status, String storageConditions, LocalDateTime receivedDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.batchNumber = batchNumber;
        this.lotNumber = lotNumber;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
        this.costPerUnit = costPerUnit;
        this.manufacturingDate = manufacturingDate;
        this.expirationDate = expirationDate;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.status = status;
        this.storageConditions = storageConditions;
        this.receivedDate = receivedDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void releaseQuantity(Integer quantity) {
        this.availableQuantity += quantity;
        this.updatedAt = LocalDateTime.now();
        if (this.status == BatchStatus.DEPLETED && this.availableQuantity > 0) {
            this.status = BatchStatus.ACTIVE;
        }
    }

    public void allocateQuantity(Integer quantity) {
        validateForUse();
        if (this.availableQuantity < quantity) {
            throw new IllegalStateException("Insufficient quantity in batch");
        }
        this.availableQuantity -= quantity;
        this.updatedAt = LocalDateTime.now();
        if (this.availableQuantity <= 0) {
            this.status = BatchStatus.DEPLETED;
        }
    }


    public void validateForUse() {
        if (this.status != BatchStatus.ACTIVE) {
            throw new InvalidBatchException("Batch is not active");
        }
        if (isExpired()) {
            throw new BatchExpiredException("Batch has expired");
        }
        if (this.availableQuantity <= 0) {
            throw new InvalidBatchException("Batch has no available quantity");
        }
    }

    public void markAsExpired() {
        this.status = BatchStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsRecalled() {
        this.status = BatchStatus.RECALLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDamaged() {
        this.status = BatchStatus.DAMAGED;
        this.updatedAt = LocalDateTime.now();
    }

    public void quarantine() {
        this.status = BatchStatus.QUARANTINED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return this.expirationDate.isBefore(LocalDateTime.now());
    }

    public boolean isExpiringSoon(Integer warningDays) {
        long daysUntilExpiration = ChronoUnit.DAYS.between(LocalDateTime.now(), this.expirationDate);
        return daysUntilExpiration <= warningDays && daysUntilExpiration > 0;
    }

    public boolean isActive() {
        return this.status == BatchStatus.ACTIVE && !isExpired() && this.availableQuantity > 0;
    }

    public static InventoryBatchReconstructor reconstructor() {
        return new InventoryBatchReconstructor();
    }

    public static class InventoryBatchReconstructor {
        private BatchId id;
        private InventoryId inventoryId;
        private String batchNumber;
        private String lotNumber;
        private Integer quantity;
        private Integer availableQuantity;
        private BigDecimal costPerUnit;
        private LocalDateTime manufacturingDate;
        private LocalDateTime expirationDate;
        private String supplierId;
        private String supplierName;
        private BatchStatus status;
        private String storageConditions;
        private LocalDateTime receivedDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public InventoryBatchReconstructor id(BatchId id) {
            this.id = id;
            return this;
        }

        public InventoryBatchReconstructor inventoryId(InventoryId inventoryId) {
            this.inventoryId = inventoryId;
            return this;
        }

        public InventoryBatchReconstructor batchNumber(String batchNumber) {
            this.batchNumber = batchNumber;
            return this;
        }

        public InventoryBatchReconstructor lotNumber(String lotNumber) {
            this.lotNumber = lotNumber;
            return this;
        }

        public InventoryBatchReconstructor quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public InventoryBatchReconstructor availableQuantity(Integer availableQuantity) {
            this.availableQuantity = availableQuantity;
            return this;
        }

        public InventoryBatchReconstructor costPerUnit(BigDecimal costPerUnit) {
            this.costPerUnit = costPerUnit;
            return this;
        }

        public InventoryBatchReconstructor manufacturingDate(LocalDateTime manufacturingDate) {
            this.manufacturingDate = manufacturingDate;
            return this;
        }

        public InventoryBatchReconstructor expirationDate(LocalDateTime expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public InventoryBatchReconstructor supplierId(String supplierId) {
            this.supplierId = supplierId;
            return this;
        }

        public InventoryBatchReconstructor supplierName(String supplierName) {
            this.supplierName = supplierName;
            return this;
        }

        public InventoryBatchReconstructor status(BatchStatus status) {
            this.status = status;
            return this;
        }

        public InventoryBatchReconstructor storageConditions(String storageConditions) {
            this.storageConditions = storageConditions;
            return this;
        }

        public InventoryBatchReconstructor receivedDate(LocalDateTime receivedDate) {
            this.receivedDate = receivedDate;
            return this;
        }

        public InventoryBatchReconstructor createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public InventoryBatchReconstructor updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public InventoryBatch reconstruct() {
            return new InventoryBatch(id, inventoryId, batchNumber, lotNumber, quantity, availableQuantity, costPerUnit, manufacturingDate, expirationDate, supplierId, supplierName, status, storageConditions, receivedDate, createdAt, updatedAt);
        }
    }
}
