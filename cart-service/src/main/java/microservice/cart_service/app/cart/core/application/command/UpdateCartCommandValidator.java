package microservice.cart_service.app.cart.core.application.command;

import java.util.Map;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;

/**
 * Utility class for validating UpdateCartCommand parameters.
 * Separates validation logic for better testability and reusability.
 */
public class UpdateCartCommandValidator {

  /**
   * Validates that the customer ID is not null and contains a valid value.
   *
   * @param customerId the customer ID to validate
   * @throws IllegalArgumentException if the customer ID is null or has a null
   *                                  value
   */
  public static void validateCustomerId(CustomerId customerId) {
    if (customerId == null || customerId.value() == null) {
      throw new IllegalArgumentException("CustomerId cannot be null");
    }
  }

  /**
   * Validates that the product quantities map is not null or empty.
   *
   * @param productQuantitiesMap the map to validate
   * @throws IllegalArgumentException if the map is null or empty
   */
  public static void validateProductQuantitiesMap(Map<ProductId, Integer> productQuantitiesMap) {
    if (productQuantitiesMap == null || productQuantitiesMap.isEmpty()) {
      throw new IllegalArgumentException("Items cannot be null or empty");
    }
  }

  /**
   * Validates that all quantities in the map are positive numbers.
   *
   * @param productQuantitiesMap the map to validate
   * @throws IllegalArgumentException if any quantity is null or non-positive
   */
  public static void validateQuantities(Map<ProductId, Integer> productQuantitiesMap) {
    productQuantitiesMap.forEach((productId, quantity) -> {
      if (quantity == null || quantity <= 0) {
        throw new IllegalArgumentException(
            "Quantity for product " + productId + " must be a positive number");
      }
    });
  }

  /**
   * Performs complete validation of all UpdateCartCommand parameters.
   *
   * @param customerId           the customer ID to validate
   * @param productQuantitiesMap the product quantities map to validate
   * @throws IllegalArgumentException if any validation fails
   */
  public static void validateAll(CustomerId customerId, Map<ProductId, Integer> productQuantitiesMap) {
    validateCustomerId(customerId);
    validateProductQuantitiesMap(productQuantitiesMap);
    validateQuantities(productQuantitiesMap);
  }
}
