package microservice.cart_service.app.cart.core.application.usecases;

import microservice.cart_service.app.cart.core.application.queries.SearchCartsQuery;
import microservice.cart_service.app.cart.core.domain.exception.CartNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import microservice.cart_service.app.cart.core.application.queries.GetCartByCustomerIdQuery;
import microservice.cart_service.app.cart.core.application.queries.GetCartByIdQuery;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.port.in.usecase.CartQueryUseCase;
import microservice.cart_service.app.cart.core.port.out.CartRepository;

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
