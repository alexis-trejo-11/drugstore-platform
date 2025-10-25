package microservice.inventory_service.external.order.application.command;

import lombok.Builder;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record InsertOrderCommand(
        OrderId orderId,
        String supplierId,
        String supplierName,
        List<OrderItemCommand> items,
        LocalDateTime expectedDeliveryDate,
        String deliveryLocation,
        UserId createdBy,
        boolean isUpdate
) {
}
