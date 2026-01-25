package microservice.cart_service.app.cart.core.application.usecases.command;

import microservice.cart_service.app.cart.core.application.command.BuyCartCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuyCartUseCase {

  public void execute(BuyCartCommand command) {
    // Implementation for buying the cart
  }
}
