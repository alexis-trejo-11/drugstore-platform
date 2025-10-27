package microservice.inventory_service.internal.purachse_order.application.query;

import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.PurchaseOrderId;

public record GetOrderByIdQuery(PurchaseOrderId purchaseOrderId) {
    public static GetOrderByIdQuery of(String orderId) {
        return new GetOrderByIdQuery(new PurchaseOrderId(orderId));
    }


}


