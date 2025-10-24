package microservice.inventory_service.internal.core.stock.application.handler;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.stock.application.query.GetActiveReservationsQuery;
import microservice.inventory_service.internal.core.stock.domain.valueobject.StockReservation;
import microservice.inventory_service.internal.core.stock.port.output.StockReservationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetActiveReservationsQueryHandler {
    private final StockReservationRepository reservationRepository;

    public List<StockReservation> handle(GetActiveReservationsQuery query) {
        return reservationRepository.findActiveReservationsByInventoryId(query.inventoryId());
    }
}
