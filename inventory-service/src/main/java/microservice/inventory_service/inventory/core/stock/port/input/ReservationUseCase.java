package microservice.inventory_service.inventory.core.stock.port.input;

import microservice.inventory_service.inventory.core.stock.application.command.ConfirmReservationCommand;
import microservice.inventory_service.inventory.core.stock.application.command.ReleaseReservationCommand;
import microservice.inventory_service.inventory.core.stock.application.command.ReserveStockCommand;
import microservice.inventory_service.inventory.core.stock.application.query.GetActiveReservationsQuery;
import microservice.inventory_service.inventory.core.stock.domain.entity.StockReservation;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationId;

import java.util.List;

public interface ReservationUseCase {
    ReservationId reserveStock(ReserveStockCommand command);
    void confirmReservation(ConfirmReservationCommand command);
    void releaseReservation(ReleaseReservationCommand command);
    List<StockReservation> getActiveReservations(GetActiveReservationsQuery query);
    void cancelReservation(ReservationId reservationId);
}