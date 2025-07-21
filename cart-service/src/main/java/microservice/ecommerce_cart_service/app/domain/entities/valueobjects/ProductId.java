package microservice.ecommerce_cart_service.app.domain.entities.valueobjects;

import microservice.ecommerce_cart_service.app.shared.AbstractId;

import java.util.UUID;

public class ProductId extends AbstractId {
    public ProductId(UUID value) {
        super(value);
    }
}
