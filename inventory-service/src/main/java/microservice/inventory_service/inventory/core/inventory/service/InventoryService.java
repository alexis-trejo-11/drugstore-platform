package microservice.inventory_service.inventory.core.inventory.service;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.batch.application.handler.GetExpiringBatchesQueryHandler;
import microservice.inventory_service.inventory.core.batch.application.query.GetExpiringBatchesQuery;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.command.AdjustInventoryCommand;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.command.CreateInventoryCommand;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.handler.command.AdjustInventoryCommandHandler;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.handler.command.CreateInventoryCommandHandler;
import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.handler.query.GetInventoryByIdQueryHandler;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.handler.query.GetInventoryByProductQueryHandler;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.handler.query.GetInventoryMovementsQueryHandler;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.handler.query.GetLowStockInventoriesQueryHandler;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.query.GetInventoryByIdQuery;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.query.GetInventoryByProductQuery;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.query.GetInventoryMovementsQuery;
import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.inventory.port.Input.InventoryUseCase;
import microservice.inventory_service.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.inventory.core.stock.application.query.GetLowStockInventoriesQuery;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService implements InventoryUseCase {
    // Handlers for Commands
    private final CreateInventoryCommandHandler createInventoryHandler;
    private final AdjustInventoryCommandHandler adjustInventoryHandler;
    // Handlers for Queries
    private final GetExpiringBatchesQueryHandler getExpiringBatchesQueryHandler;
    private final GetInventoryByProductQueryHandler getInventoryByProductQueryHandler;
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
    public Inventory getInventoryByProduct(GetInventoryByProductQuery query) {
        return getInventoryByProductQueryHandler.handle(query);
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
    public Page<InventoryBatch> getExpiringInventoryBatches(GetExpiringBatchesQuery query) {
        return getExpiringBatchesQueryHandler.handle(query);
    }

}

