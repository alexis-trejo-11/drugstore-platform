package io.github.alexisTrejo11.drugstore.carts.cart.core.port.in.usecase;

import io.github.alexisTrejo11.drugstore.carts.cart.core.application.command.*;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.Cart;

public interface CartCommandUseCase {
  void createCart(CreateCartCommand command);

  Cart updateCartItems(UpdateCartCommand command);

  void moveItemToAfterwards(CreateAfterwardsCommand command);

  void removeItemFromAfterwards(RemoveAfterwardsCommand command);

  void clearCart(ClearCartCommand command);
}
