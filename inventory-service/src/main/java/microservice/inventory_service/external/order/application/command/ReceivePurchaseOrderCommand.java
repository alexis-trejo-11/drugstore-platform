package microservice.inventory_service.external.order.application.command;

import lombok.Builder;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrderId;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReceivePurchaseOrderCommand(
        PurchaseOrderId purchaseOrderId,
        List<ReceivedItemCommand> receivedItems,
        LocalDateTime receivedDate,
        UserId receivedBy
) {
}
