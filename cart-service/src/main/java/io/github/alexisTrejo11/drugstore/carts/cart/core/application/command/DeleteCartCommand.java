package io.github.alexisTrejo11.drugstore.carts.cart.core.application.command;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartValidationException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartId;

public record DeleteCartCommand(CartId cartId) {
	public DeleteCartCommand {
		if (cartId == null) {
			throw new CartValidationException("CartId cannot be null");
		}
	}
}
