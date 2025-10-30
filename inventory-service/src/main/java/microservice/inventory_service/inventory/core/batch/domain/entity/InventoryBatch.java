package microservice.inventory_service.inventory.core.batch.domain.entity;

import lombok.Getter;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchStatus;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.CreateBatchParams;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.UpdateBatchBasicInfoParams;
import microservice.inventory_service.inventory.core.batch.domain.exception.InvalidBatchFieldException;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.batch.domain.exception.BatchExpiredException;
import microservice.inventory_service.inventory.core.batch.domain.exception.InvalidBatchException;
import microservice.inventory_service.shared.domain.order.BaseDomainEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
public class InventoryBatch extends BaseDomainEntity<BatchId> {
    private final InventoryId inventoryId;
    private String batchNumber;
    private String lotNumber;
    private final Integer quantity;
    private Integer availableQuantity;
    private LocalDateTime manufacturingDate;
    private LocalDateTime expirationDate;
    private String supplierId;
    private String supplierName;
    private BatchStatus status;
    private String storageConditions;
    private final LocalDateTime receivedDate;
    private static final Logger log = LoggerFactory.getLogger(InventoryBatch.class);


    private InventoryBatch(BatchId id, InventoryId inventoryId, String batchNumber, String lotNumber, Integer quantity, Integer availableQuantity, LocalDateTime manufacturingDate, LocalDateTime expirationDate, String supplierId, String supplierName, BatchStatus status, String storageConditions, LocalDateTime receivedDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.inventoryId = inventoryId;
        this.batchNumber = batchNumber;
        this.lotNumber = lotNumber;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
        this.manufacturingDate = manufacturingDate;
        this.expirationDate = expirationDate;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.status = status;
        this.storageConditions = storageConditions;
        this.receivedDate = receivedDate;
    }

    public static InventoryBatch create(CreateBatchParams params) {
        log.info("Creating new InventoryBatch with params: {}", params);
        validateParameters(params);

        var batch = InventoryBatch.reconstructor()
                .id(BatchId.generate())
                .inventoryId(params.inventoryId())
                .batchNumber(params.batchNumber())
                .lotNumber(params.lotNumber())
                .quantity(params.quantity())
                .availableQuantity(params.quantity())
                .manufacturingDate(params.manufacturingDate())
                .expirationDate(params.expirationDate())
                .supplierId(params.supplierId())
                .supplierName(params.supplierName())
                .status(BatchStatus.ACTIVE)
                .storageConditions(params.storageConditions())
                .receivedDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .reconstruct();

        log.info("Created InventoryBatch: Id {}", batch.getId());
        return batch;
    }

    public void updateBasicInfo(UpdateBatchBasicInfoParams params) {
        log.info("Updating InventoryBatch {} with params: {}", this.id, params);

        this.batchNumber = params.batchNumber() != null ? params.batchNumber() : this.batchNumber;
        this.lotNumber = params.lotNumber() != null ? params.lotNumber() : this.lotNumber;
        this.manufacturingDate = params.manufacturingDate() != null ? params.manufacturingDate() : this.manufacturingDate;
        this.expirationDate = params.expirationDate() != null ? params.expirationDate() : this.expirationDate;
        this.supplierId = params.supplierId() != null ? params.supplierId() : this.supplierId;
        this.supplierName = params.supplierName() != null ? params.supplierName() : this.supplierName;
        this.storageConditions = params.storageConditions() != null ? params.storageConditions() : this.storageConditions;
        this.updatedAt = LocalDateTime.now();

        log.info("Updated InventoryBatch {} successfully", this.id);
    }

    private static void validateParameters(CreateBatchParams params) {
        if (params == null) {
            throw new InvalidBatchFieldException("CreateBatchParams cannot be null");
        }
        if (params.quantity() <= 0) {
            throw new InvalidBatchFieldException("Quantity must be positive");
        }
        if (params.expirationDate() != null && params.expirationDate().isBefore(LocalDateTime.now())) {
            throw new InvalidBatchFieldException("Expiration date cannot be in the past");
        }
    }

    public void releaseQuantity(Integer quantity) {
        log.info("Releasing quantity {} back to batch {}", quantity, this.id);
        this.availableQuantity += quantity;
        this.updatedAt = LocalDateTime.now();

        if (this.status == BatchStatus.DEPLETED && this.availableQuantity > 0) {
            this.status = BatchStatus.ACTIVE;
        }

        log.info("Released quantity. New available quantity: {} for batch {}", this.availableQuantity, this.id);
    }

    public void allocateQuantity(Integer quantity) {
        log.info("Allocating quantity {} from batch {}", quantity, this.id);

        validateForUse();
        if (this.availableQuantity < quantity) {
            throw new InvalidBatchException("Insufficient quantity in batch");
        }

        this.availableQuantity -= quantity;
        this.updatedAt = LocalDateTime.now();

        if (this.availableQuantity <= 0) {
            this.status = BatchStatus.DEPLETED;
        }

        log.info("Allocated quantity. New available quantity: {} for batch {}", this.availableQuantity, this.id);
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

    public Boolean isExpired() {
        if (this.expirationDate == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(this.expirationDate);
    }

    public Boolean isExpiringSoon(Integer warningDays) {
        if (this.expirationDate == null) {
            return false;
        }

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
            return new InventoryBatch(id, inventoryId, batchNumber, lotNumber, quantity, availableQuantity, manufacturingDate, expirationDate, supplierId, supplierName, status, storageConditions, receivedDate, createdAt, updatedAt);
        }
    }
}
