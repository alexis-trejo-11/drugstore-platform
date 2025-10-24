package microservice.inventory_service.internal.core.stock.application.handler;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.core.movement.domain.InventoryMovement;
import microservice.inventory_service.internal.core.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory_service.internal.core.movement.port.InventoryMovementRepository;
import microservice.inventory_service.internal.core.inventory.port.InventoryOutputPort;
import microservice.inventory_service.internal.core.inventory.domain.service.InventoryAllocationService;
import microservice.inventory_service.internal.core.stock.application.command.ReserveStockCommand;
import microservice.inventory_service.internal.core.stock.domain.ReservationId;
import microservice.inventory_service.internal.core.stock.domain.StockReservation;
import microservice.inventory_service.internal.core.stock.domain.entity.StockReservationFactory;
import microservice.inventory_service.internal.core.stock.domain.port.output.StockReservationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReserveStockCommandHandler {
    private final InventoryOutputPort inventoryRepository;
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
