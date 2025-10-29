package microservice.inventory_service.order.supplier_purchase.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.order.supplier_purchase.application.command.ReceiveOrderCommand;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.order.supplier_purchase.domain.event.PurchaseOrderReceivedEvent;
import microservice.inventory_service.order.supplier_purchase.domain.exception.OrderNotFoundException;
import microservice.inventory_service.order.supplier_purchase.domain.port.output.OrderRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReceiveOrderCommandHandler {
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public void handle(ReceiveOrderCommand command) {
        PurchaseOrder purchaseOrder = orderRepository.findById(command.purchaseOrderId())
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder not found"));

        purchaseOrder.receiveItems(command.receiveItems(), command.receivedDate());
        orderRepository.save(purchaseOrder);

        produceEvent(purchaseOrder, command.receivedDate());
    }

    private void produceEvent(PurchaseOrder order, LocalDateTime receivedDate) {
        var event = PurchaseOrderReceivedEvent.from(order, receivedDate);
        eventPublisher.publishEvent(event);
    }

    /*
    private void processReceivedItem(PurchaseOrderItem purchaseOrderItem, ReceivedItemCommand receivedItem, PurchaseOrder PurchaseOrder) {
        Inventory inventory = inventoryRepository.findByProductId(purchaseOrderItem.getProductId())
                .orElseThrow(() -> new IllegalStateException("Inventory not found for product"));

        var params = CreateBatchParams.builder()
                .inventoryId(inventory.getId())
                .batchNumber(receivedItem.batchNumber())
                .quantity(receivedItem.receivedQuantity())
                .supplierId(PurchaseOrder.getSupplierId())
                .supplierName(PurchaseOrder.getSupplierName())
                .build();

        InventoryBatch batch = InventoryBatch.create(params);
        batchRepository.save(batch);

        inventory.receiveStock(receivedItem.receivedQuantity());
        inventory.addBatch(batch);
        inventoryRepository.save(inventory);

        InventoryMovement movement = InventoryMovement.create(
                CreateMovementParams.builder()
                        .inventoryId(inventory.getId())
                        .batchId(batch.getId())
                        .movementType(MovementType.RECEIPT)
                        .quantity(receivedItem.receivedQuantity())
                        .previousQuantity(inventory.getTotalQuantity() - receivedItem.receivedQuantity())
                        .newQuantity(inventory.getTotalQuantity())
                        .reason("PurchaseOrder received")
                        .referenceId(PurchaseOrder.getId().value())
                        .referenceType("ORDER")
                        .build()
        );

        movementRepository.save(movement);
        inventory.recordMovement(movement);
    }

     */
}