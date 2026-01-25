package microservice.cart_service.app.cart.core.application.usecases.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import microservice.cart_service.app.cart.core.application.command.ClearCartCommand;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.port.out.CartRepository;

@Component
public class ClearCartUseCase {
  private final CartRepository cartRepository;

  @Autowired
  public ClearCartUseCase(CartRepository cartRepository) {
    this.cartRepository = cartRepository;
  }

  public void execute(ClearCartCommand command) {
    Cart cart = cartRepository.findByCustomerIdWithItems(command.customerId())
        .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

    cart.clear();
    cartRepository.save(cart);
  }
}
