package microservice.cart_service.app.cart.core.application.usecases.command;

import microservice.cart_service.app.cart.core.application.command.RemoveAfterwardsCommand;
import microservice.cart_service.app.cart.core.domain.exception.CartNotFoundException;
import microservice.cart_service.app.cart.core.port.out.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReturnCartItemFromAfterwardsUseCase {
	private final CartRepository cartRepository;

	@Autowired
	public ReturnCartItemFromAfterwardsUseCase(CartRepository cartRepository) {
		this.cartRepository = cartRepository;
	}

	public void execute(RemoveAfterwardsCommand command) {
		var cart = cartRepository.findById(command.cartId(), true, true)
				.orElseThrow(() -> new CartNotFoundException(command.cartId()));

		cart.returnItemsFromAfterwards(command.productIds());
		cartRepository.save(cart);
	}
}
