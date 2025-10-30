package microservice.inventory_service.inventory.core.batch.domain.entity.valueobject;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdateBatchBasicInfoParams(
        String batchNumber,
        String lotNumber,
        LocalDateTime manufacturingDate,
        LocalDateTime expirationDate,
        String supplierId,
        String supplierName,
        String storageConditions
) {
}
