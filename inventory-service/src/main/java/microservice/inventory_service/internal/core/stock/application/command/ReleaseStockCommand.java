package microservice.inventory_service.internal.core.stock.application.command;


import microservice.inventory_service.internal.core.stock.domain.valueobject.ReservationId;

public record ReleaseStockCommand(ReservationId reservationID, String reason) {
}
