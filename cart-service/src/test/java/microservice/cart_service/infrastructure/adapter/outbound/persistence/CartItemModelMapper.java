package microservice.cart_service.infrastructure.adapter.outbound.persistence;

import java.util.List;

import microservice.cart_service.app.cart.core.domain.model.CartItem;

/**
 * Mock CartItemModelMapper for testing purposes.
 * This class would normally map between CartItem domain objects and
 * CartItemModel persistence objects.
 */
public interface CartItemModelMapper {
  Object toModel(CartItem cartItem);

  CartItem toDomain(Object cartItemModel);


  /**
   * Maps domain CartItems to persistence models.
   *
   * @param cartItems the domain cart items
   * @return the persistence models (mocked as empty list for testing)
   */
  List<Object> toModels(List<CartItem> cartItems);

  /**
   * Maps persistence models to domain CartItems.
   *
   * @param cartItemModels the persistence models
   * @return the domain cart items (mocked as empty list for testing)
   */
  List<CartItem> toDomains(List<Object> cartItemModels);
}
