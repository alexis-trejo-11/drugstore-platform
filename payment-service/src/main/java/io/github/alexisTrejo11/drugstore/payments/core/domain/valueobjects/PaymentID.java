package io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects;

import java.util.UUID;

public record PaymentID(String value) {
  public static PaymentID of(String value) {
    return new PaymentID(value);
  }

  public static PaymentID of(UUID uuid) {
    return new PaymentID(uuid.toString());
  }

  public static PaymentID generate() {
    return new PaymentID(UUID.randomUUID().toString());
  }
}
