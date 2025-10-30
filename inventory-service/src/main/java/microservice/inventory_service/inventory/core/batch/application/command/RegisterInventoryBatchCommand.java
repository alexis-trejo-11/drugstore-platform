package microservice.inventory_service.inventory.core.batch.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.CreateBatchParams;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;

import java.time.LocalDateTime;

@Builder
public record RegisterInventoryBatchCommand(
        InventoryId inventoryId,
        String batchNumber,
        String lotNumber,
        Integer quantity,
        LocalDateTime manufacturingDate,
        LocalDateTime expirationDate,
        String supplierId,
        String supplierName,
        String storageConditions
) {
    public RegisterInventoryBatchCommand setInventoryId(InventoryId inventoryId) {
        return new RegisterInventoryBatchCommand(
                inventoryId,
                this.batchNumber,
                this.lotNumber,
                this.quantity,
                this.manufacturingDate,
                this.expirationDate,
                this.supplierId,
                this.supplierName,
                this.storageConditions
        );
    }

    public CreateBatchParams toCreateBatchParams() {
        return CreateBatchParams.builder()
                .inventoryId(inventoryId)
                .batchNumber(batchNumber)
                .lotNumber(lotNumber)
                .quantity(quantity)
                .manufacturingDate(manufacturingDate)
                .expirationDate(expirationDate)
                .supplierId(supplierId)
                .supplierName(supplierName)
                .storageConditions(storageConditions)
                .build();
    }
}