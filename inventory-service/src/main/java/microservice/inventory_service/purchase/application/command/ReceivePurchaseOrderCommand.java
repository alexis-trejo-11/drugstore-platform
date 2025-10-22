package microservice.inventory_service.purchase.application.command;

import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrderId;

import java.time.LocalDateTime;
import java.util.List;

public record ReceivePurchaseOrderCommand(
        PurchaseOrderId purchaseOrderId,
        List<ReceivedItemCommand> receivedItems,
        LocalDateTime receivedDate,
        UserId receivedBy
) {
}
