package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port.Input;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.query.GetExpiringBatchesQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command.AdjustInventoryCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command.CreateInventoryCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query.GetInventoryByIdQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query.GetInventoryByProductQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query.GetInventoryMovementsQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.InventoryMovement;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.query.GetLowStockInventoriesQuery;
import org.springframework.data.domain.Page;

public interface InventoryUseCase {
    InventoryId createInventory(CreateInventoryCommand inventoryCommand);
    AdjustmentId adjustInventory(AdjustInventoryCommand command);

    Inventory getInventoryById(GetInventoryByIdQuery query);
    Inventory getInventoryByProduct(GetInventoryByProductQuery query);
    Page<Inventory> getLowStockInventories(GetLowStockInventoriesQuery query);
    Page<InventoryMovement> getInventoryMovements(GetInventoryMovementsQuery query);
    Page<InventoryBatch> getExpiringInventoryBatches(GetExpiringBatchesQuery query);

}