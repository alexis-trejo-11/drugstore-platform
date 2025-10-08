package microservice.order_service.orders.application.commands.request.status;

import jakarta.validation.constraints.NotNull;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.PaymentID;

public record OrderReadyToPickupCommand(
        @NotNull OrderID orderID
) {
    public static OrderReadyToPickupCommand of(String orderId) {
        return new OrderReadyToPickupCommand(OrderID.of(orderId));
    }
}
