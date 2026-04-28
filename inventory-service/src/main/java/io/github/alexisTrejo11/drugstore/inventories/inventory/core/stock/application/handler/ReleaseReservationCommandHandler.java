package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.handler;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.service.InventoryAllocationService;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.port.InventoryMovementRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command.ReleaseReservationCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.entity.StockReservation;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.service.InventoryStockService;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.port.output.StockReservationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReleaseReservationCommandHandler {
    private final StockReservationRepository reservationRepository;
    private final InventoryMovementRepository movementRepository;
    private final InventoryStockService inventoryStockService;
    private final InventoryAllocationService allocationService;

    @Transactional
    public void handle(ReleaseReservationCommand command) {
        StockReservation stockReservation = reservationRepository.findById(command.reservationId())
                .orElseThrow(() -> new IllegalStateException("Reservation not found"));

        inventoryStockService.releaseStockByOrder(stockReservation.getOrderReference());

        /*
        InventoryMovement movement = allocationService.createReleaseMovement(
                inventory,
                stockReservation.getQuantity(),
                stockReservation.getPurchaseOrderId(),
                null
        );

        movementRepository.save(movement);
        inventory.recordMovement(movement);
         */
    }
}
