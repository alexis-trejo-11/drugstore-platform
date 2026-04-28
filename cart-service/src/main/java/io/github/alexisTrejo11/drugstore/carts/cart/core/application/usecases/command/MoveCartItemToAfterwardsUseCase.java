package io.github.alexisTrejo11.drugstore.carts.cart.core.application.usecases.command;

import jakarta.transaction.Transactional;
import io.github.alexisTrejo11.drugstore.carts.cart.core.application.command.CreateAfterwardsCommand;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartNotFoundException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.Cart;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.out.CartRepository;
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
		Cart cart = cartRepository.findByCustomerIdWithItems(command.customerId())
				.orElseThrow(() -> new CartNotFoundException(command.customerId()));

		cart.moveItemsToAfterwards(command.productIds());
		cartRepository.save(cart);
	}
}
