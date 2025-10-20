package microservice.inventory.domain.entity;

import lombok.*;
import microservice.inventory.domain.entity.enums.BatchStatus;
import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
