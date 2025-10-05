package microservice.order_service.application.commands.request;

import microservice.order_service.domain.models.valueobjects.CustomerID;
import microservice.order_service.domain.models.valueobjects.OrderID;

public record UpdateOrderStatusCommand(
    CustomerID customerId,
    OrderID orderId,
    String newStatus,
    String notes
) {
    public UpdateOrderStatusCommand {
        if (customerId == null) {
            throw new IllegalArgumentException("customerId cannot be null");
        }
        if (orderId == null) {
            throw new IllegalArgumentException("orderId cannot be null");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("newStatus cannot be null or blank");
        }

    }
}
