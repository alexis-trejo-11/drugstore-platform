package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.event;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.ReservationId;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderReference;

public record StockReservedForSaleOrderEvent(ReservationId reservationId, OrderReference orderReference) {
}
