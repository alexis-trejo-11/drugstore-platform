package microservice.cart_service.app.cart.core.application.usecases.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import microservice.cart_service.app.cart.core.application.command.CreateCartCommand;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.domain.model.CreateCartParams;
import microservice.cart_service.app.cart.core.port.out.CartRepository;

@Component
public class CreateCartUseCase {
  private final CartRepository cartRepository;

  @Autowired
  public CreateCartUseCase(CartRepository cartRepository) {
    this.cartRepository = cartRepository;
  }

  public void execute(CreateCartCommand command) {
    if (cartRepository.existsByCustomerId(command.customerId())) {
      throw new IllegalArgumentException("Cart already exists for this customer");
    }

    CreateCartParams params = command.toCreateCartParams();
    Cart cart = Cart.create(params);

    cartRepository.save(cart);
  }

}
