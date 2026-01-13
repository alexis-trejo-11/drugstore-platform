package microservice.inventory_service.inventory.core.inventory.domain.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Component
public class InventoryPurchaseOrderEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(InventoryPurchaseOrderEventHandler.class);
    private final InventoryRepository inventoryRepository;
    private final InventoryBatchRepository batchRepository;
    private final InventoryMovementRepository movementRepository;

    @Autowired
    public InventoryPurchaseOrderEventHandler(
            InventoryRepository inventoryRepository,
            InventoryBatchRepository batchRepository,
            InventoryMovementRepository movementRepository) {
        this.inventoryRepository = inventoryRepository;
        this.batchRepository = batchRepository;
        this.movementRepository = movementRepository;
    }

    @EventListener
    @Transactional
    public void handlePurchaseOrderReceived(PurchaseOrderReceivedEvent event) {
        List<ReceivedItemDetail> receivedItems = event.receivedItems();
        int receivedCount = receivedItems == null ? 0 : receivedItems.size();
        logger.info("PurchaseOrderReceivedEvent received: purchaseOrderId={}, receivedItemsCount={}", event.purchaseOrderId(), receivedCount);

        if (receivedItems == null || receivedItems.isEmpty()) {
            logger.info("No received items to process for purchaseOrderId={}", event.purchaseOrderId());
            return;
        }

        for (ReceivedItemDetail item : receivedItems) {
            logger.info("Processing received item: productId={}, batchNumber={}, receivedQuantity={}, supplierId={}, supplierName={}", item.productId(), item.batchNumber(), item.receivedQuantity(), item.supplierId(), item.supplierName());
            processItemSequentially(item, event.purchaseOrderId());
        }

        logger.info("Finished processing PurchaseOrderReceivedEvent: purchaseOrderId={}", event.purchaseOrderId());
    }

    private void processItemSequentially(ReceivedItemDetail item, PurchaseOrderId purchaseOrderId) {
        InventoryBatch batch = createBatch(item);
        registerMovement(item, purchaseOrderId, batch);
    }

    private InventoryBatch createBatch(ReceivedItemDetail item) {
        logger.info("Creating batch for productId={} with batchNumber={} and quantity={}", item.productId(), item.batchNumber(), item.receivedQuantity());

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
        logger.info("Batch created: batchId={} for inventoryId={} quantity={}", batch.getId(), inventory.getId(), batch.getQuantity());

        inventory.receiveStock(item.receivedQuantity());
        inventory.addBatch(batch);
        inventoryRepository.save(inventory);

        logger.info("Inventory updated after receiving stock: inventoryId={} totalQuantity={}", inventory.getId(), inventory.getTotalQuantity());
        return batch;
    }

    private void registerMovement(ReceivedItemDetail item, PurchaseOrderId purchaseOrderId, InventoryBatch batch) {
        logger.info("Registering inventory movement for productId={}, batchId={}, quantity={} (purchaseOrderId={})",
                item.productId(), batch.getId(), item.receivedQuantity(), purchaseOrderId);

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
        logger.info("Inventory movement saved: movementId={} inventoryId={} batchId={} quantity={}", movement.getId(), inventory.getId(), batch.getId(), movement.getQuantity());

        inventory.recordMovement(movement);
        inventoryRepository.save(inventory);

        logger.info("Inventory recorded movement and persisted: inventoryId={} totalQuantity={}", inventory.getId(), inventory.getTotalQuantity());
    }
}