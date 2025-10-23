package microservice.inventory_service.inventory.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.domain.entity.valueobject.CreateBatchParams;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Builder
public record AddInventoryBatchCommand(
        InventoryId inventoryId,
        String batchNumber,
        String lotNumber,
        Integer quantity,
        BigDecimal costPerUnit,
        LocalDateTime manufacturingDate,
        LocalDateTime expirationDate,
        String supplierId,
        String supplierName,
        String storageConditions
) {
    public AddInventoryBatchCommand setInventoryId(InventoryId inventoryId) {
        return new AddInventoryBatchCommand(
                inventoryId,
                this.batchNumber,
                this.lotNumber,
                this.quantity,
                this.costPerUnit,
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
                .costPerUnit(costPerUnit)
                .manufacturingDate(manufacturingDate)
                .expirationDate(expirationDate)
                .supplierId(supplierId)
                .supplierName(supplierName)
                .storageConditions(storageConditions)
                .build();
    }
}