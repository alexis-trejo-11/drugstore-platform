package microservice.inventory_service.internal.inventory.core.inventory.port.Input;

import microservice.inventory_service.internal.inventory.core.batch.application.query.GetExpiringBatchesQuery;
import microservice.inventory_service.internal.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.inventory.core.inventory.application.cqrs.command.AdjustInventoryCommand;
import microservice.inventory_service.internal.inventory.core.inventory.application.cqrs.command.CreateInventoryCommand;
import microservice.inventory_service.internal.inventory.core.inventory.application.cqrs.query.GetInventoryByIdQuery;
import microservice.inventory_service.internal.inventory.core.inventory.application.cqrs.query.GetInventoryByProductQuery;
import microservice.inventory_service.internal.inventory.core.inventory.application.cqrs.query.GetInventoryMovementsQuery;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.internal.inventory.core.stock.application.query.GetLowStockInventoriesQuery;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryUseCase {
    InventoryId createInventory(CreateInventoryCommand inventoryCommand);
    AdjustmentId adjustInventory(AdjustInventoryCommand command);

    Inventory getInventoryById(GetInventoryByIdQuery query);
    Inventory getInventoryByProduct(GetInventoryByProductQuery query);
    Page<Inventory> getLowStockInventories(GetLowStockInventoriesQuery query);
    Page<InventoryMovement> getInventoryMovements(GetInventoryMovementsQuery query);
    List<InventoryBatch> getExpiringInventoryBatches(GetExpiringBatchesQuery query);

}