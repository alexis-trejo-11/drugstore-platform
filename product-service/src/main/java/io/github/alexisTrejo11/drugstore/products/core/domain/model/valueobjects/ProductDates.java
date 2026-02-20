package io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects;

import java.time.LocalDateTime;

import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductValueObjectException;

public record ProductDates(
    LocalDateTime manufactureDate,
    LocalDateTime expirationDate) {
  public static LocalDateTime NONE_DATE = LocalDateTime.of(1970, 1, 1, 0, 0);

  public static ProductDates create(LocalDateTime manufactureDate, LocalDateTime expirationDate) {
    if (manufactureDate == null || expirationDate == null) {
      throw new ProductValueObjectException("ProductDates", "Dates cannot be null");
    }

    if (expirationDate.isBefore(manufactureDate)) {
      throw new ProductValueObjectException("ProductDates", "Expiration date must be after manufacture date");
    }

    return new ProductDates(manufactureDate, expirationDate);
  }
}
