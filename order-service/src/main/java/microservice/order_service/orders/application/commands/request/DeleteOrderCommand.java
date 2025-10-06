package microservice.order_service.orders.application.commands.request;

import microservice.order_service.orders.domain.models.valueobjects.OrderID;

public record DeleteOrderCommand(
        OrderID orderID,
        boolean isHardDelete
) {
    public DeleteOrderCommand {
        if (orderID == null) {
            throw new IllegalArgumentException("OrderID cannot be null");
        }
    }

    public static DeleteOrderCommand softDelete(String orderID) {
        return new DeleteOrderCommand(OrderID.of(orderID), false);
    }

    public static DeleteOrderCommand hardDelete(String orderID) {
        return new DeleteOrderCommand(OrderID.of(orderID), true);
    }
}
