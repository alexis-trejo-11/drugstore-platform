package microservice.ecommerce_cart_service.app.domain.entities.valueobjects;

import microservice.ecommerce_cart_service.app.shared.AbstractId;

import java.util.UUID;

public class CustomerId extends AbstractId {
    public CustomerId(UUID value) {
        super(value);
    }

    public static CustomerId from(String value) {
        return new CustomerId(UUID.fromString(value));
    }
    public static CustomerId from(UUID value) {
        return new CustomerId(value);
    }

}
