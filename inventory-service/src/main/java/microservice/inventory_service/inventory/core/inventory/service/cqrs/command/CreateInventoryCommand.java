package microservice.inventory_service.inventory.core.inventory.service.cqrs.command;

import lombok.Builder;
import microservice.inventory_service.inventory.core.batch.application.command.RegisterInventoryBatchCommand;
import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.CreateBatchParams;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.CreateInventoryParams;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;

import java.util.Optional;

@Builder
public record CreateInventoryCommand(
        ProductId productId,
        Integer reorderLevel,
        Integer reorderQuantity,
        Integer maximumStockLevel,
        String warehouseLocation,
        Optional<RegisterInventoryBatchCommand> batchCommand
) {

    public CreateInventoryParams toCreateInventoryParams() {
        Optional<InventoryBatch> batchOptional = batchCommand.map(cmd -> InventoryBatch.create(
                CreateBatchParams.builder()
                        .batchNumber(cmd.batchNumber())
                        .lotNumber(cmd.lotNumber())
                        .quantity(cmd.quantity())
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
