package microservice.inventory_service.external.order.application.command;

import lombok.Builder;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderId;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReceiveOrderCommand(
        OrderId orderId,
        List<ReceivedItemCommand> receivedItems,
        LocalDateTime receivedDate,
        UserId receivedBy
) {
}
