package microservice.inventory_service.purchase.application.query;

import microservice.inventory_service.purchase.domain.entity.PurchaseOrderStatus;

public record GetPurchaseOrdersByStatusQuery(PurchaseOrderStatus status) {
}
