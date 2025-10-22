package microservice.inventory_service.stock.application;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.domain.service.InventoryAllocationService;
import microservice.inventory_service.stock.application.command.ConfirmReservationCommand;
import microservice.inventory_service.stock.application.command.ReleaseReservationCommand;
import microservice.inventory_service.stock.application.command.ReserveStockCommand;
import microservice.inventory_service.stock.application.handler.ConfirmReservationCommandHandler;
import microservice.inventory_service.stock.application.handler.ReleaseReservationCommandHandler;
import microservice.inventory_service.stock.application.handler.ReserveStockCommandHandler;
import microservice.inventory_service.stock.domain.ReservationId;
import microservice.inventory_service.stock.domain.StockReservation;
import microservice.inventory_service.stock.domain.port.output.StockReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockReservationOrchestrationService {

    private final ReserveStockCommandHandler reserveStockHandler;
    private final ConfirmReservationCommandHandler confirmReservationHandler;
    private final ReleaseReservationCommandHandler releaseReservationHandler;
    private final StockReservationRepository reservationRepository;
    private final InventoryAllocationService allocationService;

    @Transactional
    public ReservationId reserveStockForOrder(ReserveStockCommand command) {
        return reserveStockHandler.handle(command);
    }

    @Transactional
    public void confirmAndFulfillReservation(ConfirmReservationCommand command) {
        confirmReservationHandler.handle(command);
    }

    @Transactional
    public void releaseExpiredReservations() {
        List<StockReservation> expiredReservations =
                reservationRepository.findAllExpiredReservations(java.time.LocalDateTime.now());

        for (StockReservation reservation : expiredReservations) {
            ReleaseReservationCommand releaseCommand = new ReleaseReservationCommand(reservation.getId());
            releaseReservationHandler.handle(releaseCommand);
        }
    }
}
