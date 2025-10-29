package microservice.inventory_service.inventory.core.inventory.application.cqrs.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.CreateBatchParams;
import microservice.inventory_service.inventory.core.batch.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.inventory.port.InventoryRepository;
import microservice.inventory_service.inventory.core.movement.domain.InventoryMovement;
import microservice.inventory_service.inventory.core.movement.domain.port.InventoryMovementRepository;
import microservice.inventory_service.inventory.core.movement.domain.valueobject.CreateMovementParams;
import microservice.inventory_service.inventory.core.movement.domain.valueobject.MovementType;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.ReceivedItemDetail;
import microservice.inventory_service.order.supplier_purchase.domain.event.PurchaseOrderReceivedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class InventoryProcessingEventHandler {
    private final InventoryRepository inventoryRepository;
    private final InventoryBatchRepository batchRepository;
    private final InventoryMovementRepository movementRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePurchaseOrderReceived(PurchaseOrderReceivedEvent event) {
        for (ReceivedItemDetail item : event.receivedItems()) {
            processItemSequentially(item, event.purchaseOrderId());
        }
    }

    private void processItemSequentially(ReceivedItemDetail item, PurchaseOrderId purchaseOrderId) {
        InventoryBatch batch = createBatch(item);
        registerMovement(item, purchaseOrderId, batch);
    }

    private InventoryBatch createBatch(ReceivedItemDetail item) {
        Inventory inventory = inventoryRepository.findByProductId(item.productId())
                .orElseThrow(() -> new IllegalStateException("Inventory not found for product"));

        var params = CreateBatchParams.builder()
                .inventoryId(inventory.getId())
                .batchNumber(item.batchNumber())
                .quantity(item.receivedQuantity())
                .supplierId(item.supplierId())
                .supplierName(item.supplierName())
                .build();

        InventoryBatch batch = InventoryBatch.create(params);
        batchRepository.save(batch);

        inventory.receiveStock(item.receivedQuantity());
        inventory.addBatch(batch);
        inventoryRepository.save(inventory);

        return batch;
    }

    private void registerMovement(ReceivedItemDetail item, PurchaseOrderId purchaseOrderId, InventoryBatch batch) {
        Inventory inventory = inventoryRepository.findByProductId(item.productId())
                .orElseThrow(() -> new IllegalStateException("Inventory not found for product"));

        InventoryMovement movement = InventoryMovement.create(
                CreateMovementParams.builder()
                        .inventoryId(inventory.getId())
                        .batchId(batch.getId())
                        .movementType(MovementType.RECEIPT)
                        .quantity(item.receivedQuantity())
                        .previousQuantity(inventory.getTotalQuantity() - item.receivedQuantity())
                        .newQuantity(inventory.getTotalQuantity())
                        .reason("PurchaseOrder received")
                        .referenceId(purchaseOrderId.value())
                        .referenceType("ORDER")
                        .build()
        );

        movementRepository.save(movement);
        inventory.recordMovement(movement);
        inventoryRepository.save(inventory);
    }
}