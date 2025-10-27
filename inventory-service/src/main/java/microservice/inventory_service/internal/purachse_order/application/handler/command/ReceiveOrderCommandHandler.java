package microservice.inventory_service.internal.purachse_order.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.purachse_order.application.command.ReceiveOrderCommand;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrder;
import microservice.inventory_service.internal.purachse_order.domain.exception.OrderNotFoundException;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.inventory.core.inventory.port.InventoryRepository;
import microservice.inventory_service.internal.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.internal.inventory.core.movement.domain.port.InventoryMovementRepository;
import microservice.inventory_service.internal.inventory.core.movement.domain.valueobject.MovementType;
import microservice.inventory_service.internal.inventory.core.batch.domain.entity.valueobject.CreateBatchParams;
import microservice.inventory_service.internal.inventory.core.movement.domain.valueobject.CreateMovementParams;
import microservice.inventory_service.internal.inventory.core.batch.port.output.InventoryBatchRepository;
import microservice.inventory_service.internal.purachse_order.application.command.ReceivedItemCommand;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrderItem;
import microservice.inventory_service.internal.purachse_order.domain.port.output.OrderRepository;
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
        PurchaseOrder PurchaseOrder = orderRepository.findById(command.purchaseOrderId())
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder not found"));

        List<PurchaseOrderItem> receivedItems = command.receivedItems().stream()
                .map(receivedItem -> {
                    PurchaseOrderItem purchaseOrderItem = findOrderItem(PurchaseOrder, receivedItem.itemId());
                    purchaseOrderItem.receiveQuantity(receivedItem.receivedQuantity());
                    purchaseOrderItem.assignBatchNumber(receivedItem.batchNumber());

                    processReceivedItem(purchaseOrderItem, receivedItem, PurchaseOrder);

                    return purchaseOrderItem;
                })
                .toList();

        PurchaseOrder.receiveItems(receivedItems, command.receivedDate());
        orderRepository.save(PurchaseOrder);
    }

    private PurchaseOrderItem findOrderItem(PurchaseOrder PurchaseOrder, String itemId) {
        return PurchaseOrder.getItems().stream()
                .filter(item -> Objects.equals(item.getId(), itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("PurchaseOrder item not found"));
    }

    private void processReceivedItem(PurchaseOrderItem purchaseOrderItem, ReceivedItemCommand receivedItem, PurchaseOrder PurchaseOrder) {
        Inventory inventory = inventoryRepository.findByProductId(purchaseOrderItem.getProductId())
                .orElseThrow(() -> new IllegalStateException("Inventory not found for medicine"));


        var params = CreateBatchParams.builder()
                .inventoryId(inventory.getId())
                .batchNumber(receivedItem.batchNumber())
                .quantity(receivedItem.receivedQuantity())
                .costPerUnit(purchaseOrderItem.getUnitCost())
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
}