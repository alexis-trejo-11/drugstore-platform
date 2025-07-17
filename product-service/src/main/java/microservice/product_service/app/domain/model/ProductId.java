package microservice.product_service.app.domain.model;

import java.util.UUID;

public record ProductId(UUID value) {
    public static ProductId generate() {
        return new ProductId(UUID.randomUUID());
    }

    public static ProductId from(String value) {
        return new ProductId(UUID.fromString(value));
    }

    public static ProductId from(UUID value) {
        return new ProductId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
