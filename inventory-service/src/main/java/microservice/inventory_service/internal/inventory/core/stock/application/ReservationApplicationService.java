package microservice.inventory_service.internal.inventory.core.stock.application;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.inventory.core.stock.application.command.ConfirmReservationCommand;
import microservice.inventory_service.internal.inventory.core.stock.application.command.ReleaseReservationCommand;
import microservice.inventory_service.internal.inventory.core.stock.application.command.ReserveStockCommand;
import microservice.inventory_service.internal.inventory.core.stock.application.handler.ConfirmReservationCommandHandler;
import microservice.inventory_service.internal.inventory.core.stock.application.handler.GetActiveReservationsQueryHandler;
import microservice.inventory_service.internal.inventory.core.stock.application.handler.ReleaseReservationCommandHandler;
import microservice.inventory_service.internal.inventory.core.stock.application.handler.ReserveStockCommandHandler;
import microservice.inventory_service.internal.inventory.core.stock.application.query.GetActiveReservationsQuery;
import microservice.inventory_service.internal.inventory.core.stock.domain.valueobject.ReservationId;
import microservice.inventory_service.internal.inventory.core.stock.domain.valueobject.StockReservation;
import microservice.inventory_service.internal.inventory.core.stock.port.input.ReservationUseCase;
import microservice.inventory_service.internal.inventory.core.stock.port.output.StockReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReservationApplicationService implements ReservationUseCase {
    private final ReserveStockCommandHandler reserveHandler;
    private final ConfirmReservationCommandHandler confirmHandler;
    private final ReleaseReservationCommandHandler releaseHandler;
    private final GetActiveReservationsQueryHandler getActiveHandler;
    private final StockReservationRepository reservationRepository;

    @Override
    @Transactional
    public ReservationId reserveStock(ReserveStockCommand command) {
        return reserveHandler.handle(command);
    }

    @Override
    @Transactional
    public void confirmReservation(ConfirmReservationCommand command) {
        confirmHandler.handle(command);
    }

    @Override
    @Transactional
    public void releaseReservation(ReleaseReservationCommand command) {
        releaseHandler.handle(command);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockReservation> getActiveReservations(GetActiveReservationsQuery query) {
        return getActiveHandler.handle(query);
    }

    @Override
    @Transactional
    public void cancelReservation(ReservationId reservationId) {
        StockReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalStateException("Reservation not found"));

        reservation.cancel();
        reservationRepository.save(reservation);
    }
}
