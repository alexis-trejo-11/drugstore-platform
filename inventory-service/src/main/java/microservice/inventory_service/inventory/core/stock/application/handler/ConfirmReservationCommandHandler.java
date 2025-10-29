package microservice.inventory_service.inventory.core.stock.application.handler;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.inventory.port.InventoryRepository;
import microservice.inventory_service.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.inventory.core.inventory.domain.exception.InventoryNotFoundException;
import microservice.inventory_service.inventory.core.batch.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.core.inventory.domain.service.InventoryAllocationService;
import microservice.inventory_service.inventory.core.movement.domain.port.InventoryMovementRepository;
import microservice.inventory_service.inventory.core.stock.application.command.ConfirmReservationCommand;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.StockReservation;
import microservice.inventory_service.inventory.core.stock.port.output.StockReservationRepository;
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
                reservation.getPurchaseOrderId(),
                command.performedBy()
        );

        movementRepository.save(movement);

        inventory.recordMovement(movement);
        inventoryRepository.save(inventory);
    }
}

