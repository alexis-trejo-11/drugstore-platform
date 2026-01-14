package microservice.product_service.app.domain.model.valueobjects;

import java.util.UUID;

public record ProductID(UUID value) {
    public static ProductID generate() {
        return new ProductID(UUID.randomUUID());
    }

    public static ProductID from(String value) {
        return new ProductID(UUID.fromString(value));
    }

    public static ProductID from(UUID value) {
        return new ProductID(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
