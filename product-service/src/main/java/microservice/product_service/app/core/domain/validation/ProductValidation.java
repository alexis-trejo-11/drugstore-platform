package microservice.product_service.app.core.domain.validation;

import microservice.product_service.app.core.domain.exception.ProductValidationException;

public final class ProductValidation {
    private ProductValidation() {}

    public static void requireNonNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new ProductValidationException(fieldName + " cannot be null");
        }
    }

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ProductValidationException(fieldName + " cannot be null or blank");
        }
    }
}

