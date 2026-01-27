package microservice.cart_service.app.cart.core.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import microservice.cart_service.app.cart.core.domain.exception.CartValueObjectException;

/**
 * Value object representing an item's price in the cart.
 * Enforces business rules for valid price values.
 */
public record ItemPrice(BigDecimal value) {

  public static final BigDecimal MIN_PRICE = BigDecimal.ZERO;
  public static final BigDecimal MAX_PRICE = new BigDecimal("999999.99");
  public static final int SCALE = 2;
  public static final ItemPrice NONE = new ItemPrice(null);

  public void validate() {
    if (value == null) {
      throw new CartValueObjectException("ItemPrice", "Price cannot be null");
    }
    if (value.compareTo(MIN_PRICE) < 0) {
      throw new CartValueObjectException("ItemPrice", "Price cannot be negative");
    }
    if (value.compareTo(MAX_PRICE) > 0) {
      throw new CartValueObjectException("ItemPrice", String.format("Price cannot exceed %s", MAX_PRICE));
    }
    if (value.scale() > SCALE) {
      throw new CartValueObjectException("ItemPrice", "Price cannot have more than " + SCALE + " decimal places");
    }
  }

  /**
   * Creates an ItemPrice from a BigDecimal value.
   *
   * @param value the price value
   * @return a new ItemPrice
   * @throws CartValueObjectException if value is invalid
   */
  public static ItemPrice create(BigDecimal value) {
    var price = new ItemPrice(value);

    price.validate();

    return price;
  }

  /**
   * Creates an ItemPrice from a string representation.
   *
   * @param value the string representation of the price
   * @return a new ItemPrice
   * @throws CartValueObjectException if value is invalid
   */
  public static ItemPrice from(String value) {
    if (value == null || value.isBlank()) {
      throw new CartValueObjectException("ItemPrice", "Price string cannot be null or blank");
    }
    try {
      return new ItemPrice(new BigDecimal(value).setScale(SCALE, RoundingMode.HALF_UP));
    } catch (NumberFormatException e) {
      throw new CartValueObjectException("ItemPrice", "Invalid price format: " + value);
    }
  }

  /**
   * Creates an ItemPrice from a double value.
   *
   * @param value the double value
   * @return a new ItemPrice
   */
  public static ItemPrice from(double value) {
    return new ItemPrice(BigDecimal.valueOf(value).setScale(SCALE, RoundingMode.HALF_UP));
  }

  /**
   * Creates a zero price.
   *
   * @return an ItemPrice with value zero
   */
  public static ItemPrice zero() {
    return new ItemPrice(BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP));
  }

  /**
   * Multiplies this price by the given quantity.
   *
   * @param quantity the quantity to multiply by
   * @return a new ItemPrice representing the total
   */
  public ItemPrice multiply(Quantity quantity) {
    BigDecimal result = this.value.multiply(BigDecimal.valueOf(quantity.value()))
        .setScale(SCALE, RoundingMode.HALF_UP);
    return new ItemPrice(result);
  }

  /**
   * Multiplies this price by an integer quantity.
   *
   * @param quantity the quantity to multiply by
   * @return a new ItemPrice representing the total
   */
  public ItemPrice multiply(int quantity) {
    BigDecimal result = this.value.multiply(BigDecimal.valueOf(quantity))
        .setScale(SCALE, RoundingMode.HALF_UP);
    return new ItemPrice(result);
  }

  /**
   * Adds another price to this one.
   *
   * @param other the price to add
   * @return a new ItemPrice representing the sum
   */
  public ItemPrice add(ItemPrice other) {
    return new ItemPrice(this.value.add(other.value).setScale(SCALE, RoundingMode.HALF_UP));
  }

  /**
   * Subtracts another price from this one.
   *
   * @param other the price to subtract
   * @return a new ItemPrice representing the difference
   * @throws CartValueObjectException if resulting price is negative
   */
  public ItemPrice subtract(ItemPrice other) {
    if (other == null) {
      throw new CartValueObjectException("ItemPrice", "Cannot subtract a null price");
    }

    BigDecimal result = this.value.subtract(other.value).setScale(SCALE, RoundingMode.HALF_UP);
    if (result.compareTo(MIN_PRICE) < 0) {
      throw new CartValueObjectException("ItemPrice", "Resulting price cannot be negative");
    }
    return new ItemPrice(result);
  }

  /**
   * Checks if this price is zero.
   *
   * @return true if the price is zero
   */
  public boolean isZero() {
    return value.compareTo(BigDecimal.ZERO) == 0;
  }


  public boolean isPositive() {
    return value.compareTo(BigDecimal.ZERO) > 0;
  }

  @Override
  public String toString() {
    return value.toPlainString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ItemPrice itemPrice = (ItemPrice) o;
    return value.compareTo(itemPrice.value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value.stripTrailingZeros());
  }
}

