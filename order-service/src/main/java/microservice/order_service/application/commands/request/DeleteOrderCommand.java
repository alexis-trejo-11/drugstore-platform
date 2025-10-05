package microservice.order_service.application.commands.request;

import microservice.order_service.domain.models.valueobjects.OrderID;

public record DeleteOrderCommand(
        OrderID orderId,
        boolean isHardDelete
) {
    public DeleteOrderCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("OrderID cannot be null");
        }
    }

    public static DeleteOrderCommand softDelete(String orderId) {
        return new DeleteOrderCommand(OrderID.of(orderId), false);
    }

    public static DeleteOrderCommand hardDelete(String orderId) {
        return new DeleteOrderCommand(OrderID.of(orderId), true);
    }
}
