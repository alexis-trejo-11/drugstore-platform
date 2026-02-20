package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.handler.GetExpiringBatchesQueryHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.query.GetExpiringBatchesQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command.AdjustInventoryCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command.CreateInventoryCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.command.AdjustInventoryCommandHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.command.CreateInventoryCommandHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.query.GetInventoryByIdQueryHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.query.GetInventoryByProductQueryHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.query.GetInventoryMovementsQueryHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.query.GetLowStockInventoriesQueryHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query.GetInventoryByIdQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query.GetInventoryByProductQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query.GetInventoryMovementsQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port.Input.InventoryUseCase;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.InventoryMovement;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.query.GetLowStockInventoriesQuery;
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

