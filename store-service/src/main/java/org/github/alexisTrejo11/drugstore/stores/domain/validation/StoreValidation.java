package org.github.alexisTrejo11.drugstore.stores.domain.validation;

import org.github.alexisTrejo11.drugstore.stores.domain.exception.StoreValidationException;

public final class StoreValidation {
    private StoreValidation() {}

    public static void requireNonNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new StoreValidationException(fieldName + " cannot be null");
        }
    }

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new StoreValidationException(fieldName + " cannot be null or blank");
        }
    }
}

