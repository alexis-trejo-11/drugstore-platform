package microservice.ecommerce_cart_service.app.domain.entities.valueobjects;

import microservice.ecommerce_cart_service.app.shared.AbstractId;

import java.util.UUID;

public class CartId extends AbstractId {
    public CartId(UUID value) {
        super(value);
    }

    public static CartId from(String id) {
        return new CartId(UUID.fromString(id));
    }

    public static CartId from(UUID id) {
        return new CartId(id);
    }
}
