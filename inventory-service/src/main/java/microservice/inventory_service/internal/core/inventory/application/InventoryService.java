package microservice.inventory_service.internal.core.inventory.application;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.batch.application.handler.GetExpiringBatchesQueryHandler;
import microservice.inventory_service.internal.core.batch.application.query.GetExpiringBatchesQuery;
import microservice.inventory_service.internal.core.inventory.application.cqrs.command.AdjustInventoryCommand;
import microservice.inventory_service.internal.core.inventory.application.cqrs.command.CreateInventoryCommand;
import microservice.inventory_service.internal.core.inventory.application.cqrs.handler.command.AdjustInventoryCommandHandler;
import microservice.inventory_service.internal.core.inventory.application.cqrs.handler.command.CreateInventoryCommandHandler;
import microservice.inventory_service.internal.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.core.inventory.application.cqrs.handler.query.GetInventoryByIdQueryHandler;
import microservice.inventory_service.internal.core.inventory.application.cqrs.handler.query.GetInventoryByMedicineQueryHandler;
import microservice.inventory_service.internal.core.inventory.application.cqrs.handler.query.GetInventoryMovementsQueryHandler;
import microservice.inventory_service.internal.core.inventory.application.cqrs.handler.query.GetLowStockInventoriesQueryHandler;
import microservice.inventory_service.internal.core.inventory.application.cqrs.query.GetInventoryByIdQuery;
import microservice.inventory_service.internal.core.inventory.application.cqrs.query.GetInventoryByMedicineQuery;
import microservice.inventory_service.internal.core.inventory.application.cqrs.query.GetInventoryMovementsQuery;
import microservice.inventory_service.internal.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.AdjustmentId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.core.inventory.port.Input.InventoryInputPort;
import microservice.inventory_service.internal.core.movement.domain.InventoryMovement;
import microservice.inventory_service.internal.core.stock.application.query.GetLowStockInventoriesQuery;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService implements InventoryInputPort {
    // Handlers for Commands
    private final CreateInventoryCommandHandler createInventoryHandler;
    private final AdjustInventoryCommandHandler adjustInventoryHandler;
    // Handlers for Queries
    private final GetExpiringBatchesQueryHandler getExpiringBatchesQueryHandler;
    private final GetInventoryByMedicineQueryHandler getInventoryByMedicineQueryHandler;
    private final GetInventoryMovementsQueryHandler getInventoryMovementsQueryHandler;
    private final GetLowStockInventoriesQueryHandler getLowStockInventoriesQueryHandler;
    private final GetInventoryByIdQueryHandler getInventoryByIdQueryHandler;

    @Override
    public InventoryId createInventory(CreateInventoryCommand inventoryCommand) {
         return createInventoryHandler.handle(inventoryCommand);
    }

    // TODO: Create Event Alert after inventory adjustment
    @Override
    public AdjustmentId adjustInventory(AdjustInventoryCommand command) {
        return adjustInventoryHandler.handle(command);
    }

    @Override
    public Inventory getInventoryById(GetInventoryByIdQuery query) {
        return getInventoryByIdQueryHandler.handle(query);
    }

    @Override
    public Inventory getInventoryByMedicine(GetInventoryByMedicineQuery query) {
        return getInventoryByMedicineQueryHandler.handle(query);
    }

    @Override
    public Page<Inventory> getLowStockInventories(GetLowStockInventoriesQuery query) {
        return getLowStockInventoriesQueryHandler.handle(query);
    }

    @Override
    public Page<InventoryMovement> getInventoryMovements(GetInventoryMovementsQuery query) {
        return getInventoryMovementsQueryHandler.handle(query);
    }

    @Override
    public List<InventoryBatch> getExpiringInventoryBatches(GetExpiringBatchesQuery query) {
        return getExpiringBatchesQueryHandler.handle(query);
    }

}

