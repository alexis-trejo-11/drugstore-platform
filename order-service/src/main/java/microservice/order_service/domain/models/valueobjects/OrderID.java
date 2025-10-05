package microservice.order_service.domain.models.valueobjects;

import java.util.UUID;

public record OrderID(String value) {
    public OrderID {
        if (value == null) {
            throw new IllegalArgumentException("OrderID cannot be null");
        }
    }

    public static OrderID of(UUID value) {
        return new OrderID(value.toString());
    }

    public static OrderID of(String value) {
        return new OrderID(value);
    }

    public static OrderID generate() {
        return new OrderID(UUID.randomUUID().toString());
    }

}
