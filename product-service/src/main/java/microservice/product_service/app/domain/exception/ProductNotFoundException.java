package microservice.product_service.app.domain.exception;

import microservice.product_service.app.domain.model.valueobjects.ProductID;
import microservice.product_service.app.domain.model.valueobjects.SKU;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(ProductID id) {
        super("Product with ID " + id.value() + " not found.");
    }

    public ProductNotFoundException(SKU sku) {
        super("Product with SKU " + sku.value() + " not found.");
    }

    public  ProductNotFoundException(String message) {
        super(message);

    }
}

