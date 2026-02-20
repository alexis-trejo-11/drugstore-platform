package org.github.alexisTrejo11.drugstore.stores.domain.exception;

public class StoreNotFoundException extends StoreDomainException {
    public StoreNotFoundException(String field, String value) {
        super("Store with " + field + " '" + value + "' not found.", "STORE_NOT_FOUND");
    }
}
