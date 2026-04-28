package io.github.alexisTrejo11.drugstore.carts.cart.core.application.usecases;

import io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries.SearchCartsQuery;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries.GetCartByCustomerIdQuery;
import io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries.GetCartByIdQuery;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.Cart;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.in.usecase.CartQueryUseCase;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.out.CartRepository;

@Service
public class CartQueryUseCaseImpl implements CartQueryUseCase {
	private final CartRepository cartRepository;

	@Autowired
	public CartQueryUseCaseImpl(CartRepository cartRepository) {
		this.cartRepository = cartRepository;
	}

	@Override
	public Cart getCartById(GetCartByIdQuery query) {
		return cartRepository.findById(query.cartId(), query.includeItems(), query.includeAfterwards())
				.orElseThrow(() -> new CartNotFoundException(query.cartId()));
	}

	@Override
	public Cart getCartByCustomerId(GetCartByCustomerIdQuery query) {
		return cartRepository.findByCustomerIdWithItems(query.customerId())
				.orElseThrow(
						() -> new CartNotFoundException(query.customerId()));
	}

	@Override
	public Page<Cart> searchCarts(SearchCartsQuery query) {
		return cartRepository.search(query.criteria(), query.pageable());
	}
}
