package microservice.order_service.orders.domain.models.valueobjects;

import java.util.UUID;

public record PaymentID(String value) {
    public PaymentID {
        if (value == null) {
            throw new IllegalArgumentException("PaymentID cannot be null");
        }
    }

    public static PaymentID of(UUID value) {
        return new PaymentID(value.toString());
    }

    public static PaymentID of(String value) {
        return new PaymentID(value);
    }

    public static PaymentID generate() {
        return new PaymentID(UUID.randomUUID().toString());
    }

}
