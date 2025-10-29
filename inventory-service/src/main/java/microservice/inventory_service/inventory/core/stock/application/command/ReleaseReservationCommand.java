package microservice.inventory_service.inventory.core.stock.application.command;

import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationId;

public record ReleaseReservationCommand(
        ReservationId reservationId,
        String reason
) {
}
