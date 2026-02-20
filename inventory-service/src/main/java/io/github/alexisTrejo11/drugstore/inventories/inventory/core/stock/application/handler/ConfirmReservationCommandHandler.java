package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.handler;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port.InventoryRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.service.InventoryAllocationService;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command.ConfirmReservationCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.entity.StockReservation;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.port.output.StockReservationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ConfirmReservationCommandHandler {

    private final StockReservationRepository reservationRepository;
    private final InventoryRepository inventoryRepository;
    //private final InventoryMovementRepository movementRepository;
    private final InventoryAllocationService allocationService;

    @Transactional
    public void handle(ConfirmReservationCommand command) {
        StockReservation reservation = reservationRepository.findById(command.reservationId())
                .orElseThrow(() -> new IllegalStateException("Reservation not found"));

        if (!reservation.isActive()) {
            throw new IllegalStateException("Reservation is not active");
        }

        List<Inventory> inventories = inventoryRepository.findByIdIn(reservation.getInventoryIds());



        inventories.forEach(inventory -> {
            Integer reservedQuantity = reservation.getReservedQuantityFor(inventory.getId());
            inventory.confirmReservation(reservedQuantity);
        });

        reservation.confirmAll();

        inventoryRepository.bulkSave(inventories);
        reservationRepository.save(reservation);

         /*
        List<InventoryBatch> availableBatches = batchRepository.findByInventoryId(inventory.getId(), true);
        List<InventoryBatch> allocatedBatches = allocationService.allocateBatchesForOrder(availableBatches,reservation.getQuantity());
        allocatedBatches.forEach(batchRepository::save);
         */

        /*
        InventoryMovement movement = allocationService.createSaleMovement(
                inventory,
                reservation.getQuantity(),
                reservation.getPurchaseOrderId(),
                command.performedBy()
        );


        movementRepository.save(movement);

        inventory.recordMovement(movement);
         */

    }
}

