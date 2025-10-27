package microservice.inventory_service.internal.purachse_order.application.command;

import lombok.Builder;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.PurchaseOrderId;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReceiveOrderCommand(
        PurchaseOrderId purchaseOrderId,
        List<ReceivedItemCommand> receivedItems,
        LocalDateTime receivedDate,
        UserId receivedBy
) {
}
