package io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.handler;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.CreateBatchParams;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.port.output.InventoryBatchRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port.InventoryRepository;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.ReceivedItemDetail;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.event.PurchaseOrderReceivedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InventoryBatchEventHandler {
    private final InventoryRepository inventoryRepository;
    private final InventoryBatchRepository batchRepository;

    @EventListener
    @Transactional
    @Async
    public void handlePurchaseOrderReceived(PurchaseOrderReceivedEvent event) {
        for (ReceivedItemDetail item : event.receivedItems()) {
            createBatchForReceivedItem(item);
        }
    }

    private void createBatchForReceivedItem(ReceivedItemDetail item) {
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
    }
}