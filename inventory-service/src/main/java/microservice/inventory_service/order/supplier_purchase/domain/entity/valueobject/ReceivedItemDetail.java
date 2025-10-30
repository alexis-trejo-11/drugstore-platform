package microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject;

import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;

import java.time.LocalDateTime;


public record ReceivedItemDetail(
        ProductId productId,
        String batchNumber,
        int receivedQuantity,
        String supplierId,
        String supplierName) {
}