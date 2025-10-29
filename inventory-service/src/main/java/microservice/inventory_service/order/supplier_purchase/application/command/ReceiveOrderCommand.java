package microservice.inventory_service.order.supplier_purchase.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.ReceivedItem;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReceiveOrderCommand(
        PurchaseOrderId purchaseOrderId,
        List<ReceivedItemCommand> receivedItemCmd,
        LocalDateTime receivedDate,
        UserId receivedBy
) {

    public List<ReceivedItem> receiveItems() {
        return receivedItemCmd.stream()
                .map(item -> new ReceivedItem(
                        item.itemId(),
                        item.batchNumber(),
                        item.receivedQuantity()
                ))
                .toList();
    }
}
