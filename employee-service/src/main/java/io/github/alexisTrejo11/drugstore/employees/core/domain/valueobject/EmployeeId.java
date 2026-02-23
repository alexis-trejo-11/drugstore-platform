package io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a unique employee identifier
 */
public class EmployeeId implements Serializable {

  private final String value;

  private EmployeeId(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("EmployeeId cannot be null or empty");
    }
    this.value = value;
  }

  public static EmployeeId of(String value) {
    return new EmployeeId(value);
  }

  public static EmployeeId generate() {
    return new EmployeeId(UUID.randomUUID().toString());
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
    EmployeeId that = (EmployeeId) o;
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
