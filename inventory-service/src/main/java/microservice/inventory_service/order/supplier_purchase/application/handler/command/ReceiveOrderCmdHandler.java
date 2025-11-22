package microservice.inventory_service.order.supplier_purchase.application.handler.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.order.supplier_purchase.application.command.ReceiveOrderCommand;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.order.supplier_purchase.domain.event.PurchaseOrderReceivedEvent;
import microservice.inventory_service.order.supplier_purchase.domain.exception.OrderNotFoundException;
import microservice.inventory_service.order.supplier_purchase.domain.port.output.PurchaseOrderRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReceiveOrderCmdHandler {
    private final PurchaseOrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handle(ReceiveOrderCommand command) {
        log.info("Handling ReceiveOrderCommand for PurchaseOrder ID: {}", command.purchaseOrderId());
        PurchaseOrder purchaseOrder = orderRepository.findById(command.purchaseOrderId())
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder not found"));

        log.info("Receiving items for PurchaseOrder ID: {}", command.purchaseOrderId());
        purchaseOrder.receiveItems(command.receiveItems(), command.receivedDate());

        log.info("Saving updated PurchaseOrder ID: {}", command.purchaseOrderId());
        orderRepository.save(purchaseOrder);

        log.info("Producing PurchaseOrderReceivedEvent for PurchaseOrder ID: {}", command.purchaseOrderId());
        produceEvent(purchaseOrder, command.receivedDate());

        log.info("ReceiveOrderCommand handling completed for PurchaseOrder ID: {}", command.purchaseOrderId());
    }

    private void produceEvent(PurchaseOrder order, LocalDateTime receivedDate) {
        var event = PurchaseOrderReceivedEvent.from(order, receivedDate);
        eventPublisher.publishEvent(event);
    }
}