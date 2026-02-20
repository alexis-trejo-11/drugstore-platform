package io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartId;

public record GetCartByIdQuery(
		CartId cartId,
		boolean includeItems,
		boolean includeAfterwards) {

	public GetCartByIdQuery {
		if (cartId == null) {
			throw new IllegalArgumentException("cartId cannot be null");
		}
	}

	public static GetCartByIdQuery from(String cartId, boolean includeItems, boolean includeAfterwards) {
		return new GetCartByIdQuery(new CartId(cartId), includeItems, includeAfterwards);
	}
}
