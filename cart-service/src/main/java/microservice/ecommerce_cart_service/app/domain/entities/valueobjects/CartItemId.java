package microservice.ecommerce_cart_service.app.domain.entities.valueobjects;

import microservice.ecommerce_cart_service.app.shared.AbstractId;

import java.util.UUID;

public class CartItemId extends AbstractId {
    public CartItemId(UUID value) {
        super(value);
    }

    public static CartItemId generate() {
        return new CartItemId(UUID.randomUUID());
    }
}
