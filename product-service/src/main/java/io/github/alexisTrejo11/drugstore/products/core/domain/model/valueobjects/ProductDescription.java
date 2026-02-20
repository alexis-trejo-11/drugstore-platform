package io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects;

import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductValueObjectException;

public record ProductDescription(String value) {
    
    public static final ProductDescription EMPTY = new ProductDescription("");
    
    public static ProductDescription create(String value) {
        if (value == null) {
            return EMPTY;
        }
        
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return EMPTY;
        }
        
        if (trimmed.length() > 2000) {
            throw new ProductValueObjectException("ProductDescription", "Product description cannot exceed 2000 characters");
        }
        
        return new ProductDescription(trimmed);
    }
    
    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }
    
    public String getValue() {
        return value != null ? value : "";
    }
}