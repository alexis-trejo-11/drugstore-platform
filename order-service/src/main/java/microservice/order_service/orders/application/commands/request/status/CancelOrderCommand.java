package microservice.order_service.orders.application.commands.request.status;

import lombok.Builder;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;


public record CancelOrderCommand(
        OrderID orderID,
        UserID userID,
        String reason,
        boolean isAdminRequest
) {
    @Builder
    public CancelOrderCommand {
        if (orderID == null) {
            throw new IllegalArgumentException("OrderID cannot be null");
        }
        if (userID == null) {
            throw new IllegalArgumentException("UserID cannot be null");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Cancellation reason cannot be null or empty");
        }
    }

    public static CancelOrderCommand adminCancel(String orderID, String reason) {
        return CancelOrderCommand.builder()
                .orderID(OrderID.of(orderID))
                .userID(null) // Admin cancellation, no specific customer
                .isAdminRequest(true)
                .reason(reason)
                .build();
    }

    public static CancelOrderCommand customerCancel(String orderID, String userID, String reason) {
        return CancelOrderCommand.builder()
                .orderID(OrderID.of(orderID))
                .userID(UserID.of(userID))
                .reason(reason)
                .isAdminRequest(false)
                .build();
    }
}
