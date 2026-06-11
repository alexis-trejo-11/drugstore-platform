package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.service;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command.ReserveStockCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.handler.ReserveStockCommandHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.event.StockReservedForSaleOrderEvent;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.event.ReceiveSaleOrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InventorySaleOrderEventHandler {
    private final ReserveStockCommandHandler reserveStockCommandHandler;
    private final ApplicationEventPublisher eventPublisher;
    private final Logger logger = LoggerFactory.getLogger(InventorySaleOrderEventHandler.class);

    @Autowired
    public InventorySaleOrderEventHandler(ReserveStockCommandHandler reserveStockCommandHandler,
                                          ApplicationEventPublisher eventPublisher) {
        this.reserveStockCommandHandler = reserveStockCommandHandler;
        this.eventPublisher = eventPublisher;
    }


    @EventListener
    @Transactional
    void handleReceiveSaleOrderEvent(ReceiveSaleOrderEvent event) {
        logger.info("ReceiveSaleOrderEvent received");

        var command = new ReserveStockCommand(
                event.productQuantityMap(),
                event.orderReference(),
                "Reserve stock for sale order"
        );
        var reservationId = reserveStockCommandHandler.handle(command);
        logger.info("Stock reserved for sale order: reservationId={}", reservationId);

        eventPublisher.publishEvent(new StockReservedForSaleOrderEvent(reservationId, event.orderReference()));

        logger.info("Finished processing ReceiveSaleOrderEvent");
    }
}
