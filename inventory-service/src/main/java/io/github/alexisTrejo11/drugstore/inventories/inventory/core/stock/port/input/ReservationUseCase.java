package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.port.input;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command.ConfirmReservationCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command.ReleaseReservationCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command.ReserveStockCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.query.GetActiveReservationsQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.entity.StockReservation;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.ReservationId;

import java.util.List;

public interface ReservationUseCase {
    ReservationId reserveStock(ReserveStockCommand command);
    void confirmReservation(ConfirmReservationCommand command);
    void releaseReservation(ReleaseReservationCommand command);
    List<StockReservation> getActiveReservations(GetActiveReservationsQuery query);
    void cancelReservation(ReservationId reservationId);
}