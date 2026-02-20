package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.handler;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.query.GetActiveReservationsQuery;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.entity.StockReservation;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.port.output.StockReservationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetActiveReservationsQueryHandler {
    private final StockReservationRepository reservationRepository;

    public List<StockReservation> handle(GetActiveReservationsQuery query) {
        return reservationRepository.findActiveReservationsByInventoryId(query.inventoryId());
    }
}
