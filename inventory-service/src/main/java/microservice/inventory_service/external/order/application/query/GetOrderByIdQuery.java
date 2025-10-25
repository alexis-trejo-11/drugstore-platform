package microservice.inventory_service.external.order.application.query;

import microservice.inventory_service.external.order.domain.entity.valueobject.OrderId;

public record GetOrderByIdQuery(OrderId orderId) {
    public static GetOrderByIdQuery of(String orderId) {
        return new GetOrderByIdQuery(new OrderId(orderId));
    }


}


