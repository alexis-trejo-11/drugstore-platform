package microservice.inventory_service.inventory.core.inventory.domain.service;

import microservice.inventory_service.inventory.core.inventory.port.InventoryRepository;
import microservice.inventory_service.inventory.core.movement.domain.port.InventoryMovementRepository;
import microservice.inventory_service.inventory.core.stock.application.command.ReserveStockCommand;
import microservice.inventory_service.inventory.core.stock.application.handler.ReserveStockCommandHandler;
import microservice.inventory_service.order.sales.core.domain.event.ReceiveSaleOrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class InventorySaleOrderEventHandler {
    private final ReserveStockCommandHandler reserveStockCommandHandler;
    private final Logger logger = LoggerFactory.getLogger(InventorySaleOrderEventHandler.class);

    @Autowired
    public InventorySaleOrderEventHandler(ReserveStockCommandHandler reserveStockCommandHandler) {
        this.reserveStockCommandHandler = reserveStockCommandHandler;
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

        //TODO: Notificate other services about the reservation
        logger.info("TODO: sending notification......");


        logger.info("Finished processing ReceiveSaleOrderEvent");
    }
}
