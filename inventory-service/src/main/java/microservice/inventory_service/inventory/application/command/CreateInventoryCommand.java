package microservice.inventory_service.inventory.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.domain.entity.valueobject.CreateBatchParams;
import microservice.inventory_service.inventory.domain.entity.valueobject.CreateInventoryParams;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.MedicineId;

import java.util.Optional;

@Builder
public record CreateInventoryCommand(
        MedicineId medicineId,
        Integer initialQuantity,
        Integer reorderLevel,
        Integer reorderQuantity,
        Integer maximumStockLevel,
        String warehouseLocation,
        Optional<AddInventoryBatchCommand> batchCommand
) {

    public CreateInventoryParams toCreateInventoryParams() {
        Optional<InventoryBatch> batchOptional = batchCommand.map(cmd -> InventoryBatch.create(
                CreateBatchParams.builder()
                        .batchNumber(cmd.batchNumber())
                        .lotNumber(cmd.lotNumber())
                        .quantity(cmd.quantity())
                        .costPerUnit(cmd.costPerUnit())
                        .manufacturingDate(cmd.manufacturingDate())
                        .expirationDate(cmd.expirationDate())
                        .supplierId(cmd.supplierId())
                        .supplierName(cmd.supplierName())
                        .storageConditions(cmd.storageConditions())
                        .build()
        ));

        return CreateInventoryParams.builder()
                .medicineId(this.medicineId)
                .warehouseLocation(this.warehouseLocation)
                .reorderLevel(this.reorderLevel)
                .reorderQuantity(this.reorderQuantity)
                .maximumStockLevel(this.maximumStockLevel)
                .initialBatch(batchOptional)
                .build();
    }

    public boolean hasBatch() {
        return batchCommand.isPresent();
    }
}
