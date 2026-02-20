package io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects;

import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductValueObjectException;

public record Dosage(String value) {
    
    public static final Dosage NOT_SPECIFIED = new Dosage("NOT_SPECIFIED");
    public static final Dosage NONE = new Dosage("");
    
    public static Dosage create(String value) {
        if (value == null || value.trim().isEmpty()) {
            return NONE;
        }
        
        String trimmed = value.trim();
        
        if (trimmed.length() > 100) {
            throw new ProductValueObjectException("Dosage", "Dosage information cannot exceed 100 characters");
        }
        
        if (!trimmed.matches("^[0-9]+[a-zA-Z\\s\\-]+$")) {
            throw new ProductValueObjectException("Dosage", "Dosage format is invalid. It should start with a number followed by unit (e.g., '500 mg', '2 tablets').");
        }
        
        return new Dosage(trimmed);
    }
    
    public static Dosage createForPrescription(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ProductValueObjectException("Dosage", "Dosage is required for prescription products");
        }
        
        Dosage dosage = create(value);
        
        if (dosage.isEmpty()) {
            throw new ProductValueObjectException("Dosage", "Dosage cannot be empty for prescription products");
        }
        
        return dosage;
    }
    
    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }
    
    public boolean isSpecified() {
        return !isEmpty() && !this.equals(NOT_SPECIFIED);
    }
    
    public String getValue() {
        return value != null ? value : "";
    }
    
    public String formatForDisplay() {
        if (isEmpty()) {
            return "Not specified";
        }
        return value;
    }
}