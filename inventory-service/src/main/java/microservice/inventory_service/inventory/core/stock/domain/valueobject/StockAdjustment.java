package microservice.inventory_service.inventory.core.stock.domain.valueobject;

import lombok.*;
import microservice.inventory_service.inventory.core.inventory.domain.entity.enums.AdjustmentReason;
import microservice.inventory_service.inventory.core.inventory.domain.entity.enums.AdjustmentType;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;

import java.time.LocalDateTime;

@Getter
public class StockAdjustment {
    private AdjustmentId id;
    private InventoryId inventoryId;
    private BatchId batchId;
    private AdjustmentType adjustmentType;
    private Integer quantityBefore;
    private Integer quantityAdjusted;
    private Integer quantityAfter;
    private AdjustmentReason reason;
    private String notes;
    private UserId approvedBy;
    private UserId performedBy;
    private LocalDateTime adjustmentDate;
    private LocalDateTime createdAt;

    private StockAdjustment(AdjustmentId id, InventoryId inventoryId, BatchId batchId, AdjustmentType adjustmentType,
                            Integer quantityBefore, Integer quantityAdjusted, Integer quantityAfter,
                            AdjustmentReason reason, String notes, UserId approvedBy, UserId performedBy,
                            LocalDateTime adjustmentDate, LocalDateTime createdAt) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.batchId = batchId;
        this.adjustmentType = adjustmentType;
        this.quantityBefore = quantityBefore;
        this.quantityAdjusted = quantityAdjusted;
        this.quantityAfter = quantityAfter;
        this.reason = reason;
        this.notes = notes;
        this.approvedBy = approvedBy;
        this.performedBy = performedBy;
        this.adjustmentDate = adjustmentDate;
        this.createdAt = createdAt;
    }

    public static StockAdjustmentReconstructor reconstructor() {
        return new StockAdjustmentReconstructor();
    }

    public static class StockAdjustmentReconstructor {
        private AdjustmentId id;
        private InventoryId inventoryId;
        private BatchId batchId;
        private AdjustmentType adjustmentType;
        private Integer quantityBefore;
        private Integer quantityAdjusted;
        private Integer quantityAfter;
        private AdjustmentReason reason;
        private String notes;
        private UserId approvedBy;
        private UserId performedBy;
        private LocalDateTime adjustmentDate;
        private LocalDateTime createdAt;

        public StockAdjustmentReconstructor id(AdjustmentId id) {
            this.id = id;
            return this;
        }

        public StockAdjustmentReconstructor inventoryId(InventoryId inventoryId) {
            this.inventoryId = inventoryId;
            return this;
        }

        public StockAdjustmentReconstructor batchId(BatchId batchId) {
            this.batchId = batchId;
            return this;
        }

        public StockAdjustmentReconstructor adjustmentType(AdjustmentType adjustmentType) {
            this.adjustmentType = adjustmentType;
            return this;
        }

        public StockAdjustmentReconstructor quantityBefore(Integer quantityBefore) {
            this.quantityBefore = quantityBefore;
            return this;
        }

        public StockAdjustmentReconstructor quantityAdjusted(Integer quantityAdjusted) {
            this.quantityAdjusted = quantityAdjusted;
            return this;
        }

        public StockAdjustmentReconstructor quantityAfter(Integer quantityAfter) {
            this.quantityAfter = quantityAfter;
            return this;
        }

        public StockAdjustmentReconstructor reason(AdjustmentReason reason) {
            this.reason = reason;
            return this;
        }

        public StockAdjustmentReconstructor notes(String notes) {
            this.notes = notes;
            return this;
        }

        public StockAdjustmentReconstructor approvedBy(UserId approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }

        public StockAdjustmentReconstructor performedBy(UserId performedBy) {
            this.performedBy = performedBy;
            return this;
        }

        public StockAdjustmentReconstructor adjustmentDate(LocalDateTime adjustmentDate) {
            this.adjustmentDate = adjustmentDate;
            return this;
        }

        public StockAdjustmentReconstructor createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public StockAdjustment reconstruct() {
            return new StockAdjustment(id, inventoryId, batchId, adjustmentType, quantityBefore,
                    quantityAdjusted, quantityAfter, reason, notes, approvedBy, performedBy,
                    adjustmentDate, createdAt);
        }
    }
}
