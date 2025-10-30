package microservice.inventory_service.inventory.core.batch.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.UpdateBatchBasicInfoParams;

import java.time.LocalDateTime;

@Builder
public record UpdateInventoryBatchCommand(
        BatchId batchId,
        String batchNumber,
        String lotNumber,
        LocalDateTime manufacturingDate,
        LocalDateTime expirationDate,
        String supplierId,
        String supplierName,
        String storageConditions
) {

    public UpdateBatchBasicInfoParams toUpdateBatchBasicInfoParams() {
        return UpdateBatchBasicInfoParams.builder()
                .batchNumber(batchNumber)
                .lotNumber(lotNumber)
                .manufacturingDate(manufacturingDate)
                .expirationDate(expirationDate)
                .supplierId(supplierId)
                .supplierName(supplierName)
                .storageConditions(storageConditions)
                .build();
    }
}