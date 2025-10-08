package microservice.order_service.orders.application.commands.request.status;


import microservice.order_service.orders.domain.models.valueobjects.OrderID;

public record CompleteOrderCommand(
        OrderID orderID
) {

    public static CompleteOrderCommand of(String orderId) {
        return new CompleteOrderCommand(OrderID.of(orderId));
    }
}
