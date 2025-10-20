package microservice.stock.domain;

import lombok.*;
import microservice.inventory.domain.entity.enums.AdjustmentReason;
import microservice.inventory.domain.entity.enums.AdjustmentType;
import microservice.inventory.domain.entity.valueobject.id.AdjustmentID;
import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.entity.valueobject.id.InventoryID;
import microservice.inventory.domain.entity.valueobject.id.UserID;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustment {
    private AdjustmentID id;
    private InventoryID inventoryId;
    private BatchId batchId;
    private AdjustmentType adjustmentType;
    private Integer quantityBefore;
    private Integer quantityAdjusted;
    private Integer quantityAfter;
    private AdjustmentReason reason;
    private String notes;
    private UserID approvedBy;
    private UserID performedBy;
    private LocalDateTime adjustmentDate;
    private LocalDateTime createdAt;
}
