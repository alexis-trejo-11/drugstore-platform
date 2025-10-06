package microservice.order_service.orders.application.commands.request;

import microservice.order_service.orders.domain.models.valueobjects.UserID;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;

public record UpdateOrderStatusCommand(
    UserID userID,
    OrderID orderID,
    String newStatus,
    String notes
) {
    public UpdateOrderStatusCommand {
        if (userID == null) {
            throw new IllegalArgumentException("userID cannot be null");
        }
        if (orderID == null) {
            throw new IllegalArgumentException("orderID cannot be null");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("newStatus cannot be null or blank");
        }

    }
}
