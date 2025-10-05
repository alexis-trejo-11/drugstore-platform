package microservice.order_service.application.commands.request;

import lombok.Builder;
import microservice.order_service.domain.models.valueobjects.CustomerID;
import microservice.order_service.domain.models.valueobjects.OrderID;


public record CancelOrderCommand(
        OrderID orderId,
        CustomerID customerId,
        String reason,
        boolean isAdminRequest
) {
    @Builder
    public CancelOrderCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("OrderID cannot be null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("CustomerID cannot be null");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Cancellation reason cannot be null or empty");
        }
    }

    public static CancelOrderCommand adminCancel(String orderId, String reason) {
        return CancelOrderCommand.builder()
                .orderId(OrderID.of(orderId))
                .customerId(null) // Admin cancellation, no specific customer
                .isAdminRequest(true)
                .reason(reason)
                .build();
    }

    public static CancelOrderCommand customerCancel(String orderId, String customerId, String reason) {
        return CancelOrderCommand.builder()
                .orderId(OrderID.of(orderId))
                .customerId(CustomerID.of(customerId))
                .reason(reason)
                .isAdminRequest(false)
                .build();
    }
}
