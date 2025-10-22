package microservice.stock.application.command;


import microservice.stock.domain.ReservationId;

public record ReleaseStockCommand(ReservationId reservationID, String reason) {
}
