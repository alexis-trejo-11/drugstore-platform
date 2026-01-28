package microservice.cart_service.app.cart.core.domain.model;

import java.util.List;

import lombok.Builder;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartTimeStamps;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;

/**
 * Parameters record for reconstructing an existing Cart from persistence.
 * Contains all fields needed to fully reconstruct a cart.
 */
public record ReconstructCartParams(
    CartId id,
    CustomerId customerId,
    List<CartItem> items,
    List<AfterwardsItem> afterwardsItems,
    CartTimeStamps timeStamps) {
  /**
   * Creates reconstruction params with an empty item list.
   *
   * @param id         the cart ID
   * @param customerId the customer ID
   * @param timeStamps the timestamps
   * @return ReconstructCartParams with empty items
   */
  public static ReconstructCartParams withEmptyItems(
      CartId id,
      CustomerId customerId,
      CartTimeStamps timeStamps) {
    return new ReconstructCartParams(id, customerId, List.of(), List.of(),timeStamps);
  }
}
