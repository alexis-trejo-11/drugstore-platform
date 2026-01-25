package microservice.cart_service.app.cart.core.domain.model;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;

/**
 * Parameters record for creating a new Cart.
 * Contains only the required fields for creating a cart.
 */
public record CreateCartParams(
    CustomerId customerId) {
  /**
   * Creates parameters from a customer ID string.
   *
   * @param customerId the customer ID as string
   * @return CreateCartParams with the parsed customer ID
   */
  public static CreateCartParams fromCustomerIdString(String customerId) {
    return new CreateCartParams(CustomerId.from(customerId));
  }
}
