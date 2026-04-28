package io.github.alexisTrejo11.drugstore.products.core.domain.validation;

import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductValidationException;

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

