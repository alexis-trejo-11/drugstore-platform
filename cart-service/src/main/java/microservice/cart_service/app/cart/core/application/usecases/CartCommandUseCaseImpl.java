package microservice.cart_service.app.cart.core.application.usecases;

import lombok.RequiredArgsConstructor;

import microservice.cart_service.app.cart.core.application.command.CreateAfterwardsCommand;
import microservice.cart_service.app.cart.core.application.command.RemoveAfterwardsCommand;
import org.springframework.stereotype.Service;

import microservice.cart_service.app.cart.core.application.command.*;
import microservice.cart_service.app.cart.core.application.usecases.command.*;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.port.in.usecase.CartCommandUseCase;

@Service
@RequiredArgsConstructor
public class CartCommandUseCaseImpl implements CartCommandUseCase {
  private final CreateCartUseCase createCartUseCase;
  private final UpdateCartItemsUseCase updateCartUseCase;
  private final ClearCartUseCase clearCartUseCase;
  private final BuyCartUseCase buyCartUseCase;
  private final MoveCartItemToAfterwardsUseCase moveCartItemToAfterwards;
  private final ReturnCartItemFromAfterwardsUseCase returnCartItemFromAfterwards;

  @Override
  public void createCart(CreateCartCommand command) {
    createCartUseCase.execute(command);
  }

  @Override
  public Cart updateCartItems(UpdateCartCommand command) {
    return updateCartUseCase.execute(command);
  }

  @Override
  public void moveItemToAfterwards(CreateAfterwardsCommand command) {
    moveCartItemToAfterwards.execute(command);
  }

  @Override
  public void removeItemFromAfterwards(RemoveAfterwardsCommand command) {
    returnCartItemFromAfterwards.execute(command);
  }

  @Override
  public void clearCart(ClearCartCommand command) {
    clearCartUseCase.execute(command);
  }

  @Override
  public void buyCart(BuyCartCommand command) {
    buyCartUseCase.execute(command);
  }



}
