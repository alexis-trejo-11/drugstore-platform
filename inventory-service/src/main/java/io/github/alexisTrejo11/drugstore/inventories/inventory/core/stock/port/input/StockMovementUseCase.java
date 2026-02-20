package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.port.input;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command.AdjustInventoryCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command.TransferInventoryCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query.GetInventoryMovementsQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.InventoryMovement;
import org.springframework.data.domain.Page;


public interface StockMovementUseCase {
    AdjustmentId adjustInventory(AdjustInventoryCommand command);
    void transferInventory(TransferInventoryCommand command);
    Page<InventoryMovement> getInventoryMovements(GetInventoryMovementsQuery query);
    void releaseExpiredReservations();
}