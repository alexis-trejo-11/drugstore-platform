package microservice.inventory_service.stock.application.handler;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.domain.entity.InventoryMovement;
import microservice.inventory_service.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory_service.inventory.domain.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.domain.port.output.InventoryMovementRepository;
import microservice.inventory_service.inventory.domain.port.output.InventoryRepository;
import microservice.inventory_service.inventory.domain.service.InventoryAllocationService;
import microservice.inventory_service.stock.application.command.ConfirmReservationCommand;
import microservice.inventory_service.stock.domain.StockReservation;
import microservice.inventory_service.stock.domain.port.output.StockReservationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ConfirmReservationCommandHandler {

    private final StockReservationRepository reservationRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryBatchRepository batchRepository;
    private final InventoryMovementRepository movementRepository;
    private final InventoryAllocationService allocationService;

    @Transactional
    public void handle(ConfirmReservationCommand command) {
        StockReservation reservation = reservationRepository.findById(command.reservationId())
                .orElseThrow(() -> new IllegalStateException("Reservation not found"));

        reservation.validateActive();

        Inventory inventory = inventoryRepository.findById(reservation.getInventoryId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        List<InventoryBatch> availableBatches = batchRepository.findAvailableBatchesByInventoryId(inventory.getId());
        List<InventoryBatch> allocatedBatches = allocationService.allocateBatchesForOrder(availableBatches,reservation.getQuantity());

        allocatedBatches.forEach(batchRepository::save);

        inventory.confirmReservation(reservation.getQuantity());
        reservation.confirm();

        inventoryRepository.save(inventory);
        reservationRepository.save(reservation);

        InventoryMovement movement = allocationService.createSaleMovement(
                inventory,
                reservation.getQuantity(),
                reservation.getOrderId(),
                command.performedBy()
        );

        movementRepository.save(movement);

        inventory.recordMovement(movement);
        inventoryRepository.save(inventory);
    }
}

