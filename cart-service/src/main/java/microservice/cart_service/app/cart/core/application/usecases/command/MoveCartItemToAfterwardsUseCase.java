package microservice.cart_service.app.cart.core.application.usecases.command;

import jakarta.transaction.Transactional;
import microservice.cart_service.app.cart.core.application.command.CreateAfterwardsCommand;
import microservice.cart_service.app.cart.core.domain.exception.CartNotFoundException;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.port.out.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MoveCartItemToAfterwardsUseCase {
	private final CartRepository cartRepository;

	@Autowired
	public MoveCartItemToAfterwardsUseCase(CartRepository cartRepository) {
		this.cartRepository = cartRepository;
	}

	@Transactional
	public void execute(CreateAfterwardsCommand command) {
		Cart cart = cartRepository.findById(command.cartId(), true, true)
				.orElseThrow(() -> new CartNotFoundException(command.cartId()));

		cart.moveItemsToAfterwards(command.productIds());
		cartRepository.save(cart);
	}
}
