package microservice.inventory_service.stock.application.command;

import microservice.inventory_service.stock.domain.ReservationId;

public record ReleaseReservationCommand(
        ReservationId reservationId
) {
}
