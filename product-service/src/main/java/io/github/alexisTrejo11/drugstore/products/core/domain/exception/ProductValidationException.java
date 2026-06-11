package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

public class ProductValidationException extends ProductBaseException {
  public ProductValidationException(String message) {
    super(message, "PRODUCT_VALIDATION_EXCEPTION");
  }
}
