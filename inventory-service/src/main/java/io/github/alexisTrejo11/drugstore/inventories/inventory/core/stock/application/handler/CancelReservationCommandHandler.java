package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.handler;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.ReservationId;
import org.springframework.stereotype.Component;

@Component
public class CancelReservationCommandHandler {

    public void handle(ReservationId reservationId) {
        // Implementation for canceling a reservation
    }
}
