package io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects;

import io.github.alexisTrejo11.drugstore.payments.core.domain.exception.PaymentBusinessRuleException;
import io.github.alexisTrejo11.drugstore.payments.core.domain.validation.DomainValidation;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount, String currency) {

  public static final Money ZERO_USD = new Money(BigDecimal.ZERO, "USD");
  public static final Money ZERO_MXN = new Money(BigDecimal.ZERO, "MXN");

  public Money {
    DomainValidation.requireNonNull(amount, "Money amount");
    DomainValidation.requireNonBlank(currency, "Money currency");
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new PaymentBusinessRuleException("Money amount cannot be negative");
    }
    // Normalize to 2 decimal places
    amount = amount.setScale(2, RoundingMode.HALF_UP);
    currency = currency.toUpperCase();
  }

  public static Money of(BigDecimal amount, String currency) {
    return new Money(amount, currency);
  }

  public static Money of(double amount, String currency) {
    return new Money(BigDecimal.valueOf(amount), currency);
  }

  public Money add(Money other) {
    requireSameCurrency(other);
    return new Money(this.amount.add(other.amount), this.currency);
  }

  public Money subtract(Money other) {
    requireSameCurrency(other);
    BigDecimal result = this.amount.subtract(other.amount);
    if (result.compareTo(BigDecimal.ZERO) < 0) {
      throw new PaymentBusinessRuleException("Subtraction would result in negative money");
    }
    return new Money(result, this.currency);
  }

  public boolean isGreaterThan(Money other) {
    requireSameCurrency(other);
    return this.amount.compareTo(other.amount) > 0;
  }

  public boolean isZero() {
    return this.amount.compareTo(BigDecimal.ZERO) == 0;
  }

  private void requireSameCurrency(Money other) {
    if (!this.currency.equals(other.currency)) {
      throw new PaymentBusinessRuleException(
          "Currency mismatch: cannot operate between " + this.currency + " and " + other.currency);
    }
  }

  @Override
  public String toString() {
    return amount + " " + currency;
  }
}
