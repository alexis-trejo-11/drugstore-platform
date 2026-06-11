package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command.AdjustInventoryCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.command.TransferInventoryCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.command.AdjustInventoryCommandHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.command.TransferInventoryCommandHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.query.GetInventoryMovementsQueryHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query.GetInventoryMovementsQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.AdjustmentId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.InventoryMovement;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.entity.StockReservation;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.service.InventoryStockService;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.port.input.StockMovementUseCase;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.port.output.StockReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMovementUseCaseImpl implements StockMovementUseCase {
    private final AdjustInventoryCommandHandler adjustmentHandler;
    private final TransferInventoryCommandHandler transferHandler;
    private final GetInventoryMovementsQueryHandler getMovementsHandler;
    private final InventoryStockService inventoryStockService;
    private final StockReservationRepository stockReservationRepository;

    @Override
    public AdjustmentId adjustInventory(AdjustInventoryCommand command) {
        return adjustmentHandler.handle(command);
    }

    @Override
    public void transferInventory(TransferInventoryCommand command) {
        transferHandler.handle(command);
    }

    @Override
    public Page<InventoryMovement> getInventoryMovements(GetInventoryMovementsQuery query) {
        return getMovementsHandler.handle(query);
    }

    @Override
    public void releaseExpiredReservations() {
        List<StockReservation> expired = stockReservationRepository.findAllExpiredReservations(LocalDateTime.now());
        for (StockReservation reservation : expired) {
            try {
                inventoryStockService.releaseStockByOrder(reservation.getOrderReference());
            } catch (Exception e) {
                log.warn("Could not auto-release expired reservation {}: {}", reservation.getId(), e.getMessage());
            }
        }
    }
}
