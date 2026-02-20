package io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects;

import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductValueObjectException;

public record ActiveIngredient(String value) {
    
    public static final ActiveIngredient UNKNOWN = new ActiveIngredient("UNKNOWN");
    
    public static ActiveIngredient create(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ProductValueObjectException("ActiveIngredient", "Active ingredient cannot be null or empty");
        }

        if (value.trim().length() >= 200) {
            throw new ProductValueObjectException("ActiveIngredient", "Active ingredient is too long");
        }
        
        String trimmed = value.trim();
        
        if (trimmed.length() > 150) {
            throw new ProductValueObjectException("ActiveIngredient", "Active ingredient cannot exceed 150 characters");
        }
        

        if (!trimmed.matches("^[a-zA-Z0-9\\s\\-\\/\\.\\+\\(\\)]+$")) {
            throw new ProductValueObjectException("ActiveIngredient",
                "Active ingredient contains invalid characters. Only letters, numbers, spaces, and basic symbols are allowed.");
        }
        
        return new ActiveIngredient(trimmed);
    }
    
    public String getValue() {
        return value;
    }
    
    public String toUpperCase() {
        return value.toUpperCase();
    }
}