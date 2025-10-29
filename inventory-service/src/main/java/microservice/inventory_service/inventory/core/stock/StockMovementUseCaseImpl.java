package microservice.inventory_service.inventory.core.stock;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.inventory.application.cqrs.command.AdjustInventoryCommand;
import microservice.inventory_service.inventory.core.inventory.application.cqrs.command.TransferInventoryCommand;
import microservice.inventory_service.inventory.core.inventory.application.cqrs.handler.command.AdjustInventoryCommandHandler;
import microservice.inventory_service.inventory.core.inventory.application.cqrs.handler.command.TransferInventoryCommandHandler;
import microservice.inventory_service.inventory.core.inventory.application.cqrs.handler.query.GetInventoryMovementsQueryHandler;
import microservice.inventory_service.inventory.core.inventory.application.cqrs.query.GetInventoryMovementsQuery;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import microservice.inventory_service.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.inventory.core.stock.port.input.StockMovementUseCase;
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
