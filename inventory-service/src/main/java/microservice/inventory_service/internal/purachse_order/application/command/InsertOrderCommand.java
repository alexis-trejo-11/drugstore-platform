package microservice.inventory_service.internal.purachse_order.application.command;

import lombok.Builder;
import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

@Builder
public record InsertOrderCommand(
        PurchaseOrderId purchaseOrderId,
        String supplierId,
        String supplierName,
        List<OrderItemCommand> items,
        LocalDateTime expectedDeliveryDate,
        String deliveryLocation,
        Currency currency,
        UserId createdBy,
        boolean isUpdate
) {
}
