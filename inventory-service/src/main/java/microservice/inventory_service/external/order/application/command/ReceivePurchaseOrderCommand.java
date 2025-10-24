package microservice.inventory_service.external.order.application.command;

import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrderId;

import java.time.LocalDateTime;
import java.util.List;

public record ReceivePurchaseOrderCommand(
        PurchaseOrderId purchaseOrderId,
        List<ReceivedItemCommand> receivedItems,
        LocalDateTime receivedDate,
        UserId receivedBy
) {
}
