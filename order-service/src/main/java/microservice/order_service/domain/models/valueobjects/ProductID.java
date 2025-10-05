package microservice.order_service.domain.models.valueobjects;

import java.util.UUID;

public record ProductID(String value) {
    public ProductID {
        if (value == null) {
            throw new IllegalArgumentException("ProductID cannot be null");
        }
    }

    public static ProductID of(UUID value) {
        return new ProductID(value.toString());
    }

    public static ProductID of(String value) {
        return new ProductID(value);
    }

}
