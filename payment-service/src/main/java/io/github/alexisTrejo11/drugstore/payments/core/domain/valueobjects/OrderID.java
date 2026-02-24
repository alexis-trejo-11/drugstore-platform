package io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects;

import java.util.UUID;

public record OrderID(String value) {
  public static OrderID of(String value) {
    return new OrderID(value);
  }

  public static OrderID of(UUID uuid) {
    return new OrderID(uuid.toString());
  }

  public static OrderID generate() {
    return new OrderID(UUID.randomUUID().toString());
  }
}
