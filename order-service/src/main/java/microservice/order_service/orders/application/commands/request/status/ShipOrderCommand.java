package microservice.order_service.orders.application.commands.request.status;

import jakarta.validation.constraints.NotNull;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.PaymentID;

public record ShipOrderCommand(
        @NotNull OrderID orderID,
        @NotNull String deliveryTrackNumber

) {
    public static ShipOrderCommand of(String orderID, String deliveryTrackNumber) {
        return new ShipOrderCommand(
                OrderID.of(orderID),
                deliveryTrackNumber
        );
    }
}
