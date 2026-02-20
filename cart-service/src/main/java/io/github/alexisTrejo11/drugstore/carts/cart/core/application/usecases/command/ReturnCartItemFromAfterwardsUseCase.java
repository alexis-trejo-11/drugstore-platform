package io.github.alexisTrejo11.drugstore.carts.cart.core.application.usecases.command;

import io.github.alexisTrejo11.drugstore.carts.cart.core.application.command.RemoveAfterwardsCommand;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartNotFoundException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.out.CartRepository;
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
