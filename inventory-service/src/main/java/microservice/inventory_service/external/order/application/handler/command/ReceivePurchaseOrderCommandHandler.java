package microservice.inventory_service.external.order.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.core.movement.domain.InventoryMovement;
import microservice.inventory_service.internal.core.movement.domain.valueobject.MovementType;
import microservice.inventory_service.internal.core.batch.domain.entity.valueobject.CreateBatchParams;
import microservice.inventory_service.internal.core.movement.domain.valueobject.CreateMovementParams;
import microservice.inventory_service.internal.core.batch.port.output.InventoryBatchRepository;
import microservice.inventory_service.internal.core.movement.port.InventoryMovementRepository;
import microservice.inventory_service.internal.core.inventory.port.InventoryOutputPort;
import microservice.inventory_service.external.order.application.command.ReceivePurchaseOrderCommand;
import microservice.inventory_service.external.order.application.command.ReceivedItemCommand;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrder;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrderItem;
import microservice.inventory_service.external.order.domain.exception.PurchaseOrderNotFoundException;
import microservice.inventory_service.external.order.domain.port.output.PurchaseOrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReceivePurchaseOrderCommandHandler {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InventoryOutputPort inventoryRepository;
    private final InventoryBatchRepository batchRepository;
    private final InventoryMovementRepository movementRepository;

    @Transactional
    public void handle(ReceivePurchaseOrderCommand command) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(command.purchaseOrderId())
                .orElseThrow(() -> new PurchaseOrderNotFoundException("Purchase order not found"));

        List<PurchaseOrderItem> receivedItems = command.receivedItems().stream()
                .map(receivedItem -> {
                    PurchaseOrderItem orderItem = findOrderItem(purchaseOrder, receivedItem.itemId());
                    orderItem.receiveQuantity(receivedItem.receivedQuantity());
                    orderItem.assignBatchNumber(receivedItem.batchNumber());

                    processReceivedItem(orderItem, receivedItem, purchaseOrder);

                    return orderItem;
                })
                .toList();

        purchaseOrder.receiveItems(receivedItems, command.receivedDate());
        purchaseOrderRepository.save(purchaseOrder);
    }

    private PurchaseOrderItem findOrderItem(PurchaseOrder purchaseOrder, int itemId) {
        return purchaseOrder.getItems().stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Order item not found"));
    }

    private void processReceivedItem(PurchaseOrderItem orderItem, ReceivedItemCommand receivedItem, PurchaseOrder purchaseOrder) {
        Inventory inventory = inventoryRepository.findByMedicineId(orderItem.getMedicineId())
                .orElseThrow(() -> new IllegalStateException("Inventory not found for medicine"));


        var params = CreateBatchParams.builder()
                .inventoryId(inventory.getId())
                .batchNumber(receivedItem.batchNumber())
                .quantity(receivedItem.receivedQuantity())
                .costPerUnit(orderItem.getUnitCost())
                .supplierId(purchaseOrder.getSupplierId())
                .supplierName(purchaseOrder.getSupplierName())
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
                        .reason("Purchase order received")
                        .referenceId(purchaseOrder.getId().value())
                        .referenceType("PURCHASE_ORDER")
                        .build()
        );

        movementRepository.save(movement);
        inventory.recordMovement(movement);
    }
}