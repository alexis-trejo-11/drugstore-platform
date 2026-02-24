package io.github.alexisTrejo11.drugstore.payments.core.domain.exception;

import java.util.Map;

public class PaymentDomainException extends RuntimeException {
  private final String errorCode;

  protected PaymentDomainException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  protected PaymentDomainException(String message, String errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  protected PaymentDomainException(String message) {
    super(message);
    this.errorCode = "PAYMENT_DOMAIN_ERROR";
  }

  public String getErrorCode() {
    return errorCode;
  }

  public Map<String, Object> getLoggingContext() {
    return Map.of(
        "errorCode", errorCode,
        "exceptionType", this.getClass().getSimpleName(),
        "message", getMessage());
  }
}
