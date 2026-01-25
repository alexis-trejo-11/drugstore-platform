package microservice.cart_service.app.cart.core.domain.validation;

import microservice.cart_service.app.cart.core.domain.exception.CartValidationException;

/**
 * Utility class for common Cart validation operations.
 */
public final class CartValidation {

  private CartValidation() {
    // Prevent instantiation
  }

  /**
   * Validates that the given object is not null.
   *
   * @param obj       the object to validate
   * @param fieldName the name of the field for error messaging
   * @throws CartValidationException if the object is null
   */
  public static void requireNonNull(Object obj, String fieldName) {
    if (obj == null) {
      throw new CartValidationException(fieldName + " cannot be null");
    }
  }

  /**
   * Validates that the given string is not null or blank.
   *
   * @param value     the string to validate
   * @param fieldName the name of the field for error messaging
   * @throws CartValidationException if the string is null or blank
   */
  public static void requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new CartValidationException(fieldName + " cannot be null or blank");
    }
  }

  /**
   * Validates that the given integer is positive.
   *
   * @param value     the integer to validate
   * @param fieldName the name of the field for error messaging
   * @throws CartValidationException if the value is not positive
   */
  public static void requirePositive(int value, String fieldName) {
    if (value <= 0) {
      throw new CartValidationException(fieldName + " must be positive, got: " + value);
    }
  }

  /**
   * Validates that the given integer is non-negative.
   *
   * @param value     the integer to validate
   * @param fieldName the name of the field for error messaging
   * @throws CartValidationException if the value is negative
   */
  public static void requireNonNegative(int value, String fieldName) {
    if (value < 0) {
      throw new CartValidationException(fieldName + " cannot be negative, got: " + value);
    }
  }

  /**
   * Validates that the given value is within the specified range.
   *
   * @param value     the value to validate
   * @param min       the minimum allowed value (inclusive)
   * @param max       the maximum allowed value (inclusive)
   * @param fieldName the name of the field for error messaging
   * @throws CartValidationException if the value is outside the range
   */
  public static void requireInRange(int value, int min, int max, String fieldName) {
    if (value < min || value > max) {
      throw new CartValidationException(
          String.format("%s must be between %d and %d, got: %d", fieldName, min, max, value));
    }
  }
}
