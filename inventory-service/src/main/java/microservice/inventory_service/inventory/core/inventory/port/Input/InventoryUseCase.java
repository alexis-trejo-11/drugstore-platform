package microservice.inventory_service.inventory.core.inventory.port.Input;

import microservice.inventory_service.inventory.core.batch.application.query.GetExpiringBatchesQuery;
import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.command.AdjustInventoryCommand;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.command.CreateInventoryCommand;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.query.GetInventoryByIdQuery;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.query.GetInventoryByProductQuery;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.query.GetInventoryMovementsQuery;
import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.inventory.core.stock.application.query.GetLowStockInventoriesQuery;
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