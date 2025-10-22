package microservice.stock.application.command;

import microservice.stock.domain.ReservationId;

public record ReleaseReservationCommand(
        ReservationId reservationId
) {
}
