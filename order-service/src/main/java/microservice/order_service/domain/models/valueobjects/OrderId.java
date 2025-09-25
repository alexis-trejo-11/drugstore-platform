package microservice.order_service.domain.models.valueobjects;

import java.util.UUID;

public record OrderId(UUID value) {
    public OrderId {
        if (value == null) {
            throw new IllegalArgumentException("OrderId cannot be null");
        }
    }

    public static OrderId of(UUID value) {
        return new OrderId(value);
    }
    public static OrderId of(String value) {
        return new OrderId(UUID.fromString(value));
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
