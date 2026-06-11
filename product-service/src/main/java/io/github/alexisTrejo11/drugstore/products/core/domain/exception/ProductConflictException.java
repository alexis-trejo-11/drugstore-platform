package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

public class ProductConflictException extends ProductBaseException {
  public ProductConflictException(String message) {
    super(message, "PRODUCT_CONFLICT");
  }
}
