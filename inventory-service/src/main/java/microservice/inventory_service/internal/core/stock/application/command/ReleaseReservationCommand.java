package microservice.inventory_service.internal.core.stock.application.command;

import microservice.inventory_service.internal.core.stock.domain.ReservationId;

public record ReleaseReservationCommand(
        ReservationId reservationId
) {
}
