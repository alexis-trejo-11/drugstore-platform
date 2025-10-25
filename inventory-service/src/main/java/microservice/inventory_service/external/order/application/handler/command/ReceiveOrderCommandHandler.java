package microservice.inventory_service.external.order.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.command.ReceiveOrderCommand;
import microservice.inventory_service.external.order.domain.exception.OrderNotFoundException;
import microservice.inventory_service.internal.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.core.inventory.port.InventoryRepository;
import microservice.inventory_service.internal.core.movement.domain.InventoryMovement;
import microservice.inventory_service.internal.core.movement.domain.port.InventoryMovementRepository;
import microservice.inventory_service.internal.core.movement.domain.valueobject.MovementType;
import microservice.inventory_service.internal.core.batch.domain.entity.valueobject.CreateBatchParams;
import microservice.inventory_service.internal.core.movement.domain.valueobject.CreateMovementParams;
import microservice.inventory_service.internal.core.batch.port.output.InventoryBatchRepository;
import microservice.inventory_service.external.order.application.command.ReceivedItemCommand;
import microservice.inventory_service.external.order.domain.entity.Order;
import microservice.inventory_service.external.order.domain.entity.OrderItem;
import microservice.inventory_service.external.order.domain.port.output.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ReceiveOrderCommandHandler {
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryBatchRepository batchRepository;
    private final InventoryMovementRepository movementRepository;

    @Transactional
    public void handle(ReceiveOrderCommand command) {
        Order Order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        List<OrderItem> receivedItems = command.receivedItems().stream()
                .map(receivedItem -> {
                    OrderItem orderItem = findOrderItem(Order, receivedItem.itemId());
                    orderItem.receiveQuantity(receivedItem.receivedQuantity());
                    orderItem.assignBatchNumber(receivedItem.batchNumber());

                    processReceivedItem(orderItem, receivedItem, Order);

                    return orderItem;
                })
                .toList();

        Order.receiveItems(receivedItems, command.receivedDate());
        orderRepository.save(Order);
    }

    private OrderItem findOrderItem(Order Order, String itemId) {
        return Order.getItems().stream()
                .filter(item -> Objects.equals(item.getId(), itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Order item not found"));
    }

    private void processReceivedItem(OrderItem orderItem, ReceivedItemCommand receivedItem, Order Order) {
        Inventory inventory = inventoryRepository.findByProductId(orderItem.getProductId())
                .orElseThrow(() -> new IllegalStateException("Inventory not found for medicine"));


        var params = CreateBatchParams.builder()
                .inventoryId(inventory.getId())
                .batchNumber(receivedItem.batchNumber())
                .quantity(receivedItem.receivedQuantity())
                .costPerUnit(orderItem.getUnitCost())
                .supplierId(Order.getSupplierId())
                .supplierName(Order.getSupplierName())
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
                        .reason("Order received")
                        .referenceId(Order.getId().value())
                        .referenceType("ORDER")
                        .build()
        );

        movementRepository.save(movement);
        inventory.recordMovement(movement);
    }
}