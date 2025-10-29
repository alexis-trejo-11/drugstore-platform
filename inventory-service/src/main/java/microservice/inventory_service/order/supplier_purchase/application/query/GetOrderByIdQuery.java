package microservice.inventory_service.order.supplier_purchase.application.query;

import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;

public record GetOrderByIdQuery(PurchaseOrderId purchaseOrderId) {
    public static GetOrderByIdQuery of(String orderId) {
        return new GetOrderByIdQuery(new PurchaseOrderId(orderId));
    }


}


