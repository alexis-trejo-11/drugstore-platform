package microservice.stock.application.handler;

import lombok.RequiredArgsConstructor;
import microservice.inventory.domain.entity.Inventory;
import microservice.inventory.domain.entity.InventoryMovement;
import microservice.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory.domain.port.output.InventoryMovementRepository;
import microservice.inventory.domain.port.output.InventoryRepository;
import microservice.inventory.domain.service.InventoryAllocationService;
import microservice.inventory.domain.service.InventoryStatusService;
import microservice.stock.application.command.ReleaseReservationCommand;
import microservice.stock.domain.StockReservation;
import microservice.stock.domain.port.output.StockReservationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReleaseReservationCommandHandler {

    private final StockReservationRepository reservationRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;
    private final InventoryAllocationService allocationService;

    @Transactional
    public void handle(ReleaseReservationCommand command) {
        StockReservation reservation = reservationRepository.findById(command.reservationId())
                .orElseThrow(() -> new IllegalStateException("Reservation not found"));

        Inventory inventory = inventoryRepository.findById(reservation.getInventoryId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        inventory.releaseReservation(reservation.getQuantity());
        reservation.release();

        inventoryRepository.save(inventory);
        reservationRepository.save(reservation);

        InventoryMovement movement = allocationService.createReleaseMovement(
                inventory,
                reservation.getQuantity(),
                reservation.getOrderId(),
                null
        );

        movementRepository.save(movement);
        inventory.recordMovement(movement);
    }
}
