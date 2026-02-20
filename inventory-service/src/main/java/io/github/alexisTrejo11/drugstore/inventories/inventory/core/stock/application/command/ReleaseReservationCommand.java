package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.ReservationId;

public record ReleaseReservationCommand(
        ReservationId reservationId,
        String reason
) {
}
