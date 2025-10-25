package microservice.inventory_service.internal.core.inventory.application.cqrs.command;

import lombok.Builder;
import microservice.inventory_service.internal.core.batch.application.command.AddInventoryBatchCommand;
import microservice.inventory_service.internal.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.core.batch.domain.entity.valueobject.CreateBatchParams;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.CreateInventoryParams;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.ProductId;

import java.util.Optional;

@Builder
public record CreateInventoryCommand(
        ProductId productId,
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
                .productId(this.productId)
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
