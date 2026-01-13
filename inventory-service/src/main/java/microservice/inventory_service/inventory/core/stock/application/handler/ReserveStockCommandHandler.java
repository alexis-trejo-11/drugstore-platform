package microservice.inventory_service.inventory.core.stock.application.handler;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.inventory.domain.service.InventoryAllocationService;
import microservice.inventory_service.inventory.core.stock.application.command.ReserveStockCommand;
import microservice.inventory_service.inventory.core.stock.domain.service.InventoryStockService;

import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReserveStockCommandHandler {
    //private final InventoryMovementRepository movementRepository;
    private final InventoryStockService stockService;

    public ReservationId handle(ReserveStockCommand command) {
        var reservationId = stockService.reserveStock(
                command.productQuantityMap(),
                command.orderReference(),
                "Reserved for sale-order"
        );

        /*
        TODO: Create Reservation Service to handle this logic
        InventoryMovement movement = allocationService.createReservationMovement(inventory, command.quantity(), command.purchaseOrderId(), null);
        movementRepository.save(movement);
        inventory.recordMovement(movement);
         */

        return reservationId;
    }
}
