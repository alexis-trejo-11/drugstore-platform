package io.github.alexisTrejo11.drugstore.carts.cart.core.application.usecases.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.carts.cart.core.application.command.ClearCartCommand;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartNotFoundException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.Cart;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.out.CartRepository;

@Component
public class ClearCartUseCase {
  private final CartRepository cartRepository;

  @Autowired
  public ClearCartUseCase(CartRepository cartRepository) {
    this.cartRepository = cartRepository;
  }

  public void execute(ClearCartCommand command) {
    Cart cart = cartRepository.findByCustomerIdWithItems(command.customerId())
        .orElseThrow(() -> new CartNotFoundException(command.customerId()));

    cart.clear();
    cartRepository.save(cart);
  }
}
