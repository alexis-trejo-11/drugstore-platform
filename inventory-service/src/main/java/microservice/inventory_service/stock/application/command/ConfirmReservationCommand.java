package microservice.inventory_service.stock.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;
import microservice.inventory_service.stock.domain.ReservationId;

@Builder
public record ConfirmReservationCommand(ReservationId reservationId, UserId performedBy) {
}
