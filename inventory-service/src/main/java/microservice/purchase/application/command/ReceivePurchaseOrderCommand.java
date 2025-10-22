package microservice.purchase.application.command;

import microservice.inventory.domain.entity.valueobject.id.UserId;
import microservice.purchase.domain.entity.PurchaseOrderId;

import java.time.LocalDateTime;
import java.util.List;

public record ReceivePurchaseOrderCommand(
        PurchaseOrderId purchaseOrderId,
        List<ReceivedItemCommand> receivedItems,
        LocalDateTime receivedDate,
        UserId receivedBy
) {
    public List<ReceivedItem> toReceivedItems() {
        if (receivedItems == null) {
            return List.of();
        }
        return receivedItems.stream()
                .map(item -> new ReceivedItem(
                        item.itemId(),
                        item.receivedQuantity(),
                        item.batchNumber()
                ))
                .toList();
    }
}
