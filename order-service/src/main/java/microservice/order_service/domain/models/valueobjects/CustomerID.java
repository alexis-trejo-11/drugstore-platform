package microservice.order_service.domain.models.valueobjects;

import java.util.UUID;

public record CustomerID(String value) {
    public CustomerID {
        if (value == null) {
            throw new IllegalArgumentException("CustomerID cannot be null");
        }
    }

    public static CustomerID of(UUID value) {
        return new CustomerID(value.toString());
    }
    public static CustomerID of(String value) {
        return new CustomerID(value);
    }

}


