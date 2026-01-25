package microservice.cart_service.app.cart.core.port.in.usecase;

import microservice.cart_service.app.cart.core.application.command.CreateAfterwardsCommand;
import microservice.cart_service.app.cart.core.application.command.RemoveAfterwardsCommand;
import microservice.cart_service.app.cart.core.application.command.*;
import microservice.cart_service.app.cart.core.domain.model.Cart;

public interface CartCommandUseCase {
  void createCart(CreateCartCommand command);

  Cart updateCartItems(UpdateCartCommand command);

  void moveItemToAfterwards(CreateAfterwardsCommand command);

  void removeItemFromAfterwards(RemoveAfterwardsCommand command);

  void buyCart(BuyCartCommand command);

  void clearCart(ClearCartCommand command);
}
