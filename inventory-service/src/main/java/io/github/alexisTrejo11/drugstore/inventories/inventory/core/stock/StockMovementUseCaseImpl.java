package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command.AdjustInventoryCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command.TransferInventoryCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.command.AdjustInventoryCommandHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.command.TransferInventoryCommandHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.query.GetInventoryMovementsQueryHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query.GetInventoryMovementsQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.InventoryMovement;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.port.input.StockMovementUseCase;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockMovementUseCaseImpl implements StockMovementUseCase {
    private final AdjustInventoryCommandHandler adjustmentHandler;
    private final TransferInventoryCommandHandler transferHandler;
    private final GetInventoryMovementsQueryHandler getMovementsHandler;

    @Override
    public AdjustmentId adjustInventory(AdjustInventoryCommand command) {
        return adjustmentHandler.handle(command);
    }

    @Override
    public void transferInventory(TransferInventoryCommand command) {
        transferHandler.handle(command);
    }

    @Override
    public Page<InventoryMovement> getInventoryMovements(GetInventoryMovementsQuery query) {
        return getMovementsHandler.handle(query);
    }

    @Override
    public void releaseExpiredReservations() {

    }
}
