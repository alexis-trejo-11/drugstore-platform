package microservice.stock.application.handler;

import lombok.RequiredArgsConstructor;
import microservice.inventory.domain.entity.Inventory;
import microservice.inventory.domain.entity.InventoryMovement;
import microservice.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory.domain.port.output.InventoryMovementRepository;
import microservice.inventory.domain.port.output.InventoryRepository;
import microservice.inventory.domain.service.InventoryAllocationService;
import microservice.inventory.domain.service.InventoryStatusService;
import microservice.stock.application.command.ReserveStockCommand;
import microservice.stock.domain.ReservationId;
import microservice.stock.domain.StockReservation;
import microservice.stock.domain.entity.StockReservationFactory;
import microservice.stock.domain.port.output.StockReservationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReserveStockCommandHandler {
    private final InventoryRepository inventoryRepository;
    private final StockReservationRepository reservationRepository;
    private final InventoryMovementRepository movementRepository;
    private final InventoryAllocationService allocationService;

    @Transactional
    public ReservationId handle(ReserveStockCommand command) {
        Inventory inventory = inventoryRepository.findById(command.inventoryId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        inventory.reserveStock(command.quantity());
        inventoryRepository.save(inventory);

        StockReservation reservation = StockReservationFactory.create(
                command.inventoryId(),
                command.orderId(),
                command.quantity(),
                command.reservationDurationMinutes(),
                command.reason()
        );

        StockReservation savedReservation = reservationRepository.save(reservation);

        InventoryMovement movement = allocationService.createReservationMovement(
                inventory,
                command.quantity(),
                command.orderId(),
                null
        );

        movementRepository.save(movement);
        inventory.recordMovement(movement);

        return savedReservation.getId();
    }
}
