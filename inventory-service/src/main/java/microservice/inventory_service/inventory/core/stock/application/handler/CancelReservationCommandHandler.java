package microservice.inventory_service.inventory.core.stock.application.handler;

import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationId;
import org.springframework.stereotype.Component;

@Component
public class CancelReservationCommandHandler {

    public void handle(ReservationId reservationId) {
        // Implementation for canceling a reservation
    }
}
