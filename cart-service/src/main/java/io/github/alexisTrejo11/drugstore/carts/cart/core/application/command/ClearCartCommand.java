package io.github.alexisTrejo11.drugstore.carts.cart.core.application.command;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartValidationException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;

public record ClearCartCommand(CustomerId customerId) {
	public ClearCartCommand {
		if (customerId == null) {
			throw new CartValidationException("CustomerId cannot be null");
		}
	}
}
