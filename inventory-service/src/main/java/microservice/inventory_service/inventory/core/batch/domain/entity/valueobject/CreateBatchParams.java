package microservice.inventory_service.inventory.core.batch.domain.entity.valueobject;

import lombok.Builder;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record CreateBatchParams(
        InventoryId inventoryId, String batchNumber, String lotNumber,
        Integer quantity,
        LocalDateTime manufacturingDate, LocalDateTime expirationDate,
        String supplierId, String supplierName, String storageConditions
) {
}
