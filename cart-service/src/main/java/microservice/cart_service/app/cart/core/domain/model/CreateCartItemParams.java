package microservice.cart_service.app.cart.core.domain.model;

import java.math.BigDecimal;

import lombok.Builder;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ItemPrice;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.Quantity;

/**
 * Parameters record for creating a new CartItem.
 * Contains only the required fields for creating a cart item.
 */
@Builder
public record CreateCartItemParams(
    CartId cartId,
    ProductId productId,
    Quantity quantity
) {

  /**
   * Creates parameters with default quantity of 1 and no discount.
   */
  public static CreateCartItemParams withDefaults(
      CartId cartId,
      ProductId productId,
      Quantity quantity
      ) {
    return new CreateCartItemParams(cartId, productId, Quantity.one());
  }
}
