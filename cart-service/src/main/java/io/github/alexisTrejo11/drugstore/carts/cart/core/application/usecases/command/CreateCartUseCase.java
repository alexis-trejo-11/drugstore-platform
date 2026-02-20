package io.github.alexisTrejo11.drugstore.carts.cart.core.application.usecases.command;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.carts.cart.core.application.command.CreateCartCommand;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.Cart;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.CreateCartParams;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.out.CartRepository;

@Component
public class CreateCartUseCase {
  private final CartRepository cartRepository;

  @Autowired
  public CreateCartUseCase(CartRepository cartRepository) {
    this.cartRepository = cartRepository;
  }

  public void execute(CreateCartCommand command) {
    if (cartRepository.existsByCustomerId(command.customerId())) {
      throw new CartConflictException("Cart already exists for this customer");
    }

    CreateCartParams params = command.toCreateCartParams();
    Cart cart = Cart.create(params);

    cartRepository.save(cart);
  }

}
