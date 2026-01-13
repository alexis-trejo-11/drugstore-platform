package microservice.inventory_service.inventory.core.stock.application.handler;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.inventory.domain.service.InventoryAllocationService;
import microservice.inventory_service.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.inventory.core.movement.domain.port.InventoryMovementRepository;
import microservice.inventory_service.inventory.core.stock.application.command.ReleaseReservationCommand;
import microservice.inventory_service.inventory.core.stock.domain.entity.StockReservation;
import microservice.inventory_service.inventory.core.stock.domain.service.InventoryStockService;
import microservice.inventory_service.inventory.core.stock.port.output.StockReservationRepository;
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
