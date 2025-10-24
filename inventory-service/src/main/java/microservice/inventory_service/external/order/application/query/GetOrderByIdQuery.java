package microservice.inventory_service.external.order.application.query;

import microservice.inventory_service.external.order.domain.entity.valueobject.PurchaseOrderId;

public record GetOrderByIdQuery(PurchaseOrderId purchaseOrderId) {
    public static GetOrderByIdQuery of(String purchaseOrderId) {
        return new GetOrderByIdQuery(new PurchaseOrderId(purchaseOrderId));
    }


}


