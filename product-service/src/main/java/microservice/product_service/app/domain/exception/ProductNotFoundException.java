package microservice.product_service.app.domain.exception;

import microservice.product_service.app.domain.model.valueobjects.ProductID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(ProductID id) {
        super("Product with ID " + id.value() + " not found.");
    }
}

