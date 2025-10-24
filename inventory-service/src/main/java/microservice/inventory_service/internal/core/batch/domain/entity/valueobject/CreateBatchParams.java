package microservice.inventory_service.internal.core.batch.domain.entity.valueobject;

import lombok.Builder;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record CreateBatchParams(
        InventoryId inventoryId, String batchNumber, String lotNumber,
        Integer quantity, BigDecimal costPerUnit,
        LocalDateTime manufacturingDate, LocalDateTime expirationDate,
        String supplierId, String supplierName, String storageConditions
) {
}
