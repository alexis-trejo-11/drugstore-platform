package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.command.RegisterInventoryBatchCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.CreateBatchParams;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.CreateInventoryParams;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.ProductId;

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
