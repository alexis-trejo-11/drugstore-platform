package microservice.inventory_service.inventory.core.stock.port.input;

import microservice.inventory_service.inventory.core.inventory.application.cqrs.command.AdjustInventoryCommand;
import microservice.inventory_service.inventory.core.inventory.application.cqrs.command.TransferInventoryCommand;
import microservice.inventory_service.inventory.core.inventory.application.cqrs.query.GetInventoryMovementsQuery;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import microservice.inventory_service.inventory.core.movement.domain.InventoryMovement;
import org.springframework.data.domain.Page;


public interface StockMovementUseCase {
    AdjustmentId adjustInventory(AdjustInventoryCommand command);
    void transferInventory(TransferInventoryCommand command);
    Page<InventoryMovement> getInventoryMovements(GetInventoryMovementsQuery query);
    void releaseExpiredReservations();
}