package io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects;

import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductValueObjectException;

public record Manufacturer(String value) {
  public static final Manufacturer UNKNOWN = new Manufacturer("UNKNOWN");
  public static final Manufacturer NONE = new Manufacturer("");

  public static Manufacturer create(String value) {
    if (value == null || value.trim().isEmpty()) {
      return NONE;
    }

    String trimmed = value.trim();

    if (trimmed.length() > 100) {
      throw new ProductValueObjectException("Manufacturer", "Manufacturer name cannot exceed 100 characters");
    }

    if (trimmed.length() < 2) {
      throw new ProductValueObjectException("Manufacturer", "Manufacturer name must be at least 2 characters long");
    }

    return new Manufacturer(trimmed);
  }

  public static Manufacturer createRequired(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new ProductValueObjectException("Manufacturer",
          "Manufacturer is required and cannot be empty");
    }

    return create(value);
  }

  public boolean isKnown() {
    return !isEmpty() && !this.equals(UNKNOWN);
  }

  public boolean isEmpty() {
    return value == null || value.isEmpty();
  }

  public String getValue() {
    return value != null ? value : "";
  }
}
