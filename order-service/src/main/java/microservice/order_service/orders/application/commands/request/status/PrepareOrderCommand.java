package microservice.order_service.orders.application.commands.request.status;

import jakarta.validation.constraints.NotNull;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.PaymentID;

public record PrepareOrderCommand(
       @NotNull OrderID orderID
) {
    public static PrepareOrderCommand of(String orderID) {
        return new PrepareOrderCommand(
                OrderID.of(orderID)
        );
    }
}
