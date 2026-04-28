package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

public class ProductValidationException extends RuntimeException {
  public ProductValidationException(String message) {
    super(message);
  }
}
