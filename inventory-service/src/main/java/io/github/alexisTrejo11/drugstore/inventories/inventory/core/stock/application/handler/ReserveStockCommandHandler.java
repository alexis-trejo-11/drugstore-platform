package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.handler;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command.ReserveStockCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.service.InventoryStockService;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.ReservationId;
import org.springframework.stereotype.Component;

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
