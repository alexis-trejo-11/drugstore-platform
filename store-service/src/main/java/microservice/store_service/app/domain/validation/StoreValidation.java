package microservice.store_service.app.domain.validation;

import microservice.store_service.app.domain.exception.StoreValidationException;

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

