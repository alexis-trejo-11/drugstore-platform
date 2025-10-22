package microservice.stock.application.command;

import lombok.Builder;
import microservice.inventory.domain.entity.valueobject.id.UserId;
import microservice.stock.domain.ReservationId;

@Builder
public record ConfirmReservationCommand(ReservationId reservationId, UserId performedBy) {
}
