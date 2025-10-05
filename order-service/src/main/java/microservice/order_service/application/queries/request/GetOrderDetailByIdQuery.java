package microservice.order_service.application.queries.request;

import microservice.order_service.domain.models.valueobjects.OrderID;

public record GetOrderDetailByIdQuery(OrderID orderID) {
    public GetOrderDetailByIdQuery(OrderID orderID) {
        if (orderID == null) {
            throw new IllegalArgumentException("orderID cannot be null or empty");
        }
        this.orderID = orderID;
    }

    public GetOrderDetailByIdQuery of(String orderID) {
        return new GetOrderDetailByIdQuery(OrderID.of(orderID));
    }

}
