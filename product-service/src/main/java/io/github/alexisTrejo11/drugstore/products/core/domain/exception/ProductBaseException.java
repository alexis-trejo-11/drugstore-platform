package io.github.alexisTrejo11.drugstore.products.core.domain.exception;

import org.springframework.http.HttpStatus;

import libs_kernel.exceptions.DomainException;

public class ProductBaseException extends DomainException {

  public ProductBaseException(String message, HttpStatus httpStatus, String errorCode) {
    super(message, httpStatus, errorCode);
  }

  public ProductBaseException(String message, String errorCode) {
    super(message, HttpStatus.UNPROCESSABLE_ENTITY, errorCode);
  }
}
