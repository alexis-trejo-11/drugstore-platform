package io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects;

import java.util.UUID;

public record SaleID(String value) {
  public static SaleID of(String value) {
    return new SaleID(value);
  }

  public static SaleID of(UUID uuid) {
    return new SaleID(uuid.toString());
  }

  public static SaleID generate() {
    return new SaleID(UUID.randomUUID().toString());
  }
}
