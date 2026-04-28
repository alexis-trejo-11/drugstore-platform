package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.ReservationId;

@Builder
public record ConfirmReservationCommand(ReservationId reservationId, UserId performedBy) {
}
