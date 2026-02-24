package io.github.alexisTrejo11.drugstore.payments.core.domain.validation;

import java.math.BigDecimal;

import io.github.alexisTrejo11.drugstore.payments.core.domain.exception.PaymentValidationException;

public final class DomainValidation {
  private DomainValidation() {
  }

  public static void requireNonNull(Object obj, String fieldName) {
    if (obj == null) {
      throw new PaymentValidationException(fieldName + " cannot be null");
    }
  }

  public static void requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new PaymentValidationException(fieldName + " cannot be null or blank");
    }
  }

  public static void requirePositive(BigDecimal value, String fieldName) {
    if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
      throw new PaymentValidationException(fieldName + " must be positive");
    }
  }

  public static void requireNonNegative(BigDecimal value, String fieldName) {
    if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
      throw new PaymentValidationException(fieldName + " cannot be negative");
    }
  }
}
