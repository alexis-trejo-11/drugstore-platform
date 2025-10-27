package microservice.inventory_service.internal.inventory.core.stock.application.command;

import lombok.Builder;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.internal.inventory.core.stock.domain.valueobject.ReservationId;

@Builder
public record ConfirmReservationCommand(ReservationId reservationId, UserId performedBy) {
}
