package microservice.inventory_service.internal.core.stock.port.input;

import microservice.inventory_service.internal.core.inventory.application.cqrs.command.AdjustInventoryCommand;
import microservice.inventory_service.internal.core.inventory.application.cqrs.command.TransferInventoryCommand;
import microservice.inventory_service.internal.core.inventory.application.cqrs.query.GetInventoryMovementsQuery;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.AdjustmentId;
import microservice.inventory_service.internal.core.movement.domain.InventoryMovement;

import java.util.List;

public interface StockMovementUseCase {
    AdjustmentId adjustInventory(AdjustInventoryCommand command);
    void transferInventory(TransferInventoryCommand command);
    List<InventoryMovement> getInventoryMovements(GetInventoryMovementsQuery query);
}