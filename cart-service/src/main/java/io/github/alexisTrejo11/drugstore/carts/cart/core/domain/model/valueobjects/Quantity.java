package io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.InvalidQuantityException;

/**
 * Value object representing a quantity in a cart item.
 * Enforces business rules for valid quantity values.
 */
public record Quantity(int value) {

  public static final int MIN_QUANTITY = 1;
  public static final int MAX_QUANTITY = 999;

  public Quantity {
    if (value < MIN_QUANTITY) {
      throw new InvalidQuantityException(value,
          String.format("Quantity must be at least %d, got: %d", MIN_QUANTITY, value));
    }
    if (value > MAX_QUANTITY) {
      throw new InvalidQuantityException(value,
          String.format("Quantity cannot exceed %d, got: %d", MAX_QUANTITY, value));
    }
  }

  public boolean isAtMinimum() {
    return this.value == MIN_QUANTITY;
  }

  public boolean isAtMaximum() {
    return this.value == MAX_QUANTITY;
  }

  /**
   * Creates a Quantity with the specified value.
   *
   * @param value the quantity value
   * @return a new Quantity
   * @throws InvalidQuantityException if value is outside allowed range
   */
  public static Quantity of(int value) {
    return new Quantity(value);
  }

  /**
   * Creates a Quantity with a value of 1.
   *
   * @return a Quantity with value 1
   */
  public static Quantity one() {
    return new Quantity(1);
  }

  /**
   * Increases the quantity by the specified amount.
   *
   * @param amount the amount to add
   * @return a new Quantity with the increased value
   * @throws InvalidQuantityException if result exceeds maximum
   */
  public Quantity increase(int amount) {
    return new Quantity(this.value + amount);
  }

  /**
   * Decreases the quantity by the specified amount.
   *
   * @param amount the amount to subtract
   * @return a new Quantity with the decreased value
   * @throws InvalidQuantityException if result is less than minimum
   */
  public Quantity decrease(int amount) {
    return new Quantity(this.value - amount);
  }

  /**
   * Checks if adding the specified amount would exceed the maximum.
   *
   * @param amount the amount to potentially add
   * @return true if the addition would be valid
   */
  public boolean canIncrease(int amount) {
    return (this.value + amount) <= MAX_QUANTITY;
  }

  /**
   * Checks if subtracting the specified amount would be valid.
   *
   * @param amount the amount to potentially subtract
   * @return true if the subtraction would be valid
   */
  public boolean canDecrease(int amount) {
    return (this.value - amount) >= MIN_QUANTITY;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
