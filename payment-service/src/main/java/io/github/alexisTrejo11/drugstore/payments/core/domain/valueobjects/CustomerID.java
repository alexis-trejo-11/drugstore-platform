package io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects;

import java.util.UUID;

public record CustomerID(String value) {
  public static CustomerID of(String value) {
    return new CustomerID(value);
  }

  public static CustomerID of(UUID uuid) {
    return new CustomerID(uuid.toString());
  }

  public static CustomerID generate() {
    return new CustomerID(UUID.randomUUID().toString());
  }
}
