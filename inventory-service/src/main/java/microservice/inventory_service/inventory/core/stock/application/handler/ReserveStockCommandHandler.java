package microservice.inventory_service.inventory.core.stock.application.handler;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.inventory.port.InventoryRepository;
import microservice.inventory_service.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.inventory.core.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory_service.inventory.core.inventory.domain.service.InventoryAllocationService;
import microservice.inventory_service.inventory.core.movement.domain.port.InventoryMovementRepository;
import microservice.inventory_service.inventory.core.stock.application.command.ReserveStockCommand;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationId;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.StockReservation;
import microservice.inventory_service.inventory.core.stock.domain.entity.StockReservationFactory;
import microservice.inventory_service.inventory.core.stock.port.output.StockReservationRepository;
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
                command.purchaseOrderId(),
                command.quantity(),
                command.reservationDurationMinutes(),
                command.reason()
        );

        StockReservation savedReservation = reservationRepository.save(reservation);

        InventoryMovement movement = allocationService.createReservationMovement(inventory, command.quantity(), command.purchaseOrderId(), null);

        movementRepository.save(movement);
        inventory.recordMovement(movement);

        return savedReservation.getId();
    }
}
