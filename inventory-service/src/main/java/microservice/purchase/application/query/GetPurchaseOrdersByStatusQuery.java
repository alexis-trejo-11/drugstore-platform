package microservice.purchase.application.query;

import microservice.purchase.domain.entity.PurchaseOrderStatus;

public record GetPurchaseOrdersByStatusQuery(PurchaseOrderStatus status) {
}
