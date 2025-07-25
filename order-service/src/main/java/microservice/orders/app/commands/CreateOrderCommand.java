package microservice.orders.app.commands;

import microservice.orders.core.models.enums.DeliveryMethod;
import microservice.orders.core.models.valueobjects.CustomerId;
import microservice.orders.core.models.valueobjects.DeliveryAddress;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
    UUID customerId,
    List<OrderItemCommand> items,
    DeliveryMethod deliveryMethod,
    DeliveryAddress deliveryAddress,
    String notes
) {
    public CreateOrderCommand {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be null or empty");
        }
        if (deliveryMethod == null) {
            throw new IllegalArgumentException("Delivery method cannot be null");
        }
        if (deliveryMethod.requiresAddress() && deliveryAddress == null) {
            throw new IllegalArgumentException("Delivery address is required for delivery orders");
        }
    }

    public CustomerId getCustomerIdVO() {
        return CustomerId.of(customerId);
    }

    public record OrderItemCommand(
        UUID productId,
        String productName,
        double unitPrice,
        int quantity
    ) {
        public OrderItemCommand {
            if (productId == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (productName == null || productName.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be null or empty");
            }
            if (unitPrice < 0) {
                throw new IllegalArgumentException("Unit price cannot be negative");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
        }
    }
}
