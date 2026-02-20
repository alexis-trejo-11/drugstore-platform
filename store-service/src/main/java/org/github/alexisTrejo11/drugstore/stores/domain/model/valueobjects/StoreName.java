package org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects;

public record StoreName(String value) {

    public static StoreName of(String value) {
        return new StoreName(value);
    }

    public static StoreName create(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Store name cannot be null or blank.");
        }

        if (value.length() < 3 || value.length() > 100) {
            throw new IllegalArgumentException("Store name must be between 3 and 100 characters long.");
        }

        return new StoreName(value);
    }

    public static StoreName NONE = new StoreName("");
}
