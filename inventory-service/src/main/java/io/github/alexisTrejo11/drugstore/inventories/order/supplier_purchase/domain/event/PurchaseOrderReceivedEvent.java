package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.event;

import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.PurchaseOrder;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.PurchaseOrderItem;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.ReceivedItemDetail;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderReceivedEvent(PurchaseOrderId purchaseOrderId, List<ReceivedItemDetail> receivedItems, LocalDateTime receivedDate) {

    public static PurchaseOrderReceivedEvent from(PurchaseOrder order, LocalDateTime receivedDate) {
        List<ReceivedItemDetail> receivedItems = order.getItems().
                stream()
                .filter(PurchaseOrderItem::hasBeenReceived)
                .map(item -> new ReceivedItemDetail(
                        item.getProductId(),
                        item.getBatchNumber(),
                        item.getReceivedQuantity(),
                        order.getSupplierId(),
                        order.getSupplierName())
                )
                .toList();

        return new PurchaseOrderReceivedEvent(order.getId(), receivedItems, receivedDate);
    }
}