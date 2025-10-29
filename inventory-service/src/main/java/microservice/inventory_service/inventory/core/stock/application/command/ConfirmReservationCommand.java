package microservice.inventory_service.inventory.core.stock.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationId;

@Builder
public record ConfirmReservationCommand(ReservationId reservationId, UserId performedBy) {
}
