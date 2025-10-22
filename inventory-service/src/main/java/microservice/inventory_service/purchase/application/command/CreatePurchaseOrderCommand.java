package microservice.inventory_service.purchase.application.command;

import lombok.Builder;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CreatePurchaseOrderCommand(
        String supplierId,
        String supplierName,
        List<PurchaseOrderItemCommand> items,
        LocalDateTime expectedDeliveryDate,
        String deliveryLocation,
        UserId createdBy
) {
}
