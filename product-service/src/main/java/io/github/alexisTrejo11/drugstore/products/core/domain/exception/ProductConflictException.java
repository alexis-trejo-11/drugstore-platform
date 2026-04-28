package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

public class ProductConflictException extends ProductValidationException {
  public ProductConflictException(String message) {
    super(message);
  }
}
