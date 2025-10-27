package microservice.inventory_service.internal.inventory.core.movement.domain;

import lombok.Getter;
import microservice.inventory_service.internal.inventory.core.movement.domain.valueobject.MovementType;
import microservice.inventory_service.internal.inventory.core.movement.domain.valueobject.CreateMovementParams;
import microservice.inventory_service.internal.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.inventory.core.movement.domain.valueobject.MovementId;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.UserId;

import java.time.LocalDateTime;

@Getter
public class InventoryMovement {
    private MovementId id;
    private InventoryId inventoryId;
    private BatchId batchId;
    private MovementType movementType;
    private Integer quantity;
    private Integer previousQuantity;
    private Integer newQuantity;
    private String reason;
    private String referenceId;
    private String referenceType;
    private UserId performedBy;
    private String notes;
    private LocalDateTime movementDate;
    private LocalDateTime createdAt;

    private InventoryMovement(MovementId id, InventoryId inventoryId, BatchId batchId, MovementType movementType, Integer quantity, Integer previousQuantity, Integer newQuantity, String reason, String referenceId, String referenceType, UserId performedBy, String notes, LocalDateTime movementDate, LocalDateTime createdAt) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.batchId = batchId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.previousQuantity = previousQuantity;
        this.newQuantity = newQuantity;
        this.reason = reason;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.performedBy = performedBy;
        this.notes = notes;
        this.movementDate = movementDate;
        this.createdAt = createdAt;
    }

    public static InventoryMovement create(CreateMovementParams params) {
        return InventoryMovement.reconstructor()
                .id(MovementId.generate())
                .inventoryId(params.inventoryId())
                .batchId(params.batchId())
                .movementType(params.movementType())
                .quantity(params.quantity())
                .previousQuantity(params.previousQuantity())
                .newQuantity(params.newQuantity())
                .reason(params.reason())
                .referenceId(params.referenceId())
                .referenceType(params.referenceType())
                .performedBy(params.performedBy())
                .notes(params.notes())
                .movementDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .reconstruct();
    }

    public static InventoryMovement createBatchMovent(CreateMovementParams params) {
        return InventoryMovement.reconstructor()
                .id(MovementId.generate())
                .referenceType("BATCH")
                .movementType(MovementType.RECEIPT)
                .reason("Batch received")
                .notes("")
                .inventoryId(params.inventoryId())
                .batchId(params.batchId())
                .quantity(params.quantity())
                .previousQuantity(params.previousQuantity())
                .newQuantity(params.newQuantity())
                .referenceId(params.referenceId())
                .performedBy(params.performedBy())
                .movementDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .reconstruct();
    }

    public static InventoryMovementReconstructor reconstructor() {
        return new InventoryMovementReconstructor();
    }

    public static class InventoryMovementReconstructor {
        private MovementId id;
        private InventoryId inventoryId;
        private BatchId batchId;
        private MovementType movementType;
        private Integer quantity;
        private Integer previousQuantity;
        private Integer newQuantity;
        private String reason;
        private String referenceId;
        private String referenceType;
        private UserId performedBy;
        private String notes;
        private LocalDateTime movementDate;
        private LocalDateTime createdAt;

        public InventoryMovementReconstructor id(MovementId id) {
            this.id = id;
            return this;
        }

        public InventoryMovementReconstructor inventoryId(InventoryId inventoryId) {
            this.inventoryId = inventoryId;
            return this;
        }

        public InventoryMovementReconstructor batchId(BatchId batchId) {
            this.batchId = batchId;
            return this;
        }

        public InventoryMovementReconstructor movementType(MovementType movementType) {
            this.movementType = movementType;
            return this;
        }

        public InventoryMovementReconstructor quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public InventoryMovementReconstructor previousQuantity(Integer previousQuantity) {
            this.previousQuantity = previousQuantity;
            return this;
        }

        public InventoryMovementReconstructor newQuantity(Integer newQuantity) {
            this.newQuantity = newQuantity;
            return this;
        }

        public InventoryMovementReconstructor reason(String reason) {
            this.reason = reason;
            return this;
        }

        public InventoryMovementReconstructor referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public InventoryMovementReconstructor referenceType(String referenceType) {
            this.referenceType = referenceType;
            return this;
        }

        public InventoryMovementReconstructor performedBy(UserId performedBy) {
            this.performedBy = performedBy;
            return this;
        }

        public InventoryMovementReconstructor notes(String notes) {
            this.notes = notes;
            return this;
        }

        public InventoryMovementReconstructor movementDate(LocalDateTime movementDate) {
            this.movementDate = movementDate;
            return this;
        }

        public InventoryMovementReconstructor createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public InventoryMovement reconstruct() {
            return new InventoryMovement(id, inventoryId, batchId, movementType, quantity, previousQuantity, newQuantity, reason, referenceId, referenceType, performedBy, notes, movementDate, createdAt);
        }
    }
}