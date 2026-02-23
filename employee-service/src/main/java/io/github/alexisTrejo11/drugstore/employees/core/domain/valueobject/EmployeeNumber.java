package io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value object representing an employee number (badge/ID card number)
 * Format: EMP-XXXX (e.g., EMP-0001)
 */
public class EmployeeNumber implements Serializable {

  private static final String PREFIX = "EMP-";
  private final String value;

  private EmployeeNumber(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Employee number cannot be null or empty");
    }
    if (!value.matches("^EMP-\\d{4,6}$")) {
      throw new IllegalArgumentException(
          "Invalid employee number format. Expected format: EMP-XXXX (e.g., EMP-0001)");
    }
    this.value = value;
  }

  public static EmployeeNumber of(String value) {
    return new EmployeeNumber(value);
  }

  public static EmployeeNumber from(int number) {
    String formatted = String.format("%s%04d", PREFIX, number);
    return new EmployeeNumber(formatted);
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    EmployeeNumber that = (EmployeeNumber) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
