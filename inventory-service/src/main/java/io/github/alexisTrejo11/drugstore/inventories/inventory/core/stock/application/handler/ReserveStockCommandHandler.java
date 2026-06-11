package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.handler;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command.ReserveStockCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.service.InventoryStockService;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.ReservationId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReserveStockCommandHandler {
    private final InventoryStockService stockService;

    public ReservationId handle(ReserveStockCommand command) {
        return stockService.reserveStock(
                command.productQuantityMap(),
                command.orderReference(),
                command.reason() != null ? command.reason() : "Reserved for sale-order"
        );
    }
}
