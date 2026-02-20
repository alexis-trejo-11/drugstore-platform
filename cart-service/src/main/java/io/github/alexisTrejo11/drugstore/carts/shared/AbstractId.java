package io.github.alexisTrejo11.drugstore.carts.shared;

import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for ID value objects.
 * Provides common functionality for entity identifiers.
 *
 * @deprecated Consider using record-based value objects in
 *             domain.model.valueobjects package instead.
 */
@Deprecated
public abstract class AbstractId {
  private final UUID value;

  protected AbstractId(UUID value) {
    if (value == null) {
      throw new IllegalArgumentException("ID value cannot be null");
    }
    this.value = value;
  }

  public UUID getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    AbstractId that = (AbstractId) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
