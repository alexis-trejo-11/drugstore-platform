package io.github.alexisTrejo11.drugstore.carts.cart.core.application.command;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartValidationException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;

import java.util.ArrayList;
import java.util.List;

public record BuyCartCommand(
    CustomerId customerId,
    List<ProductId> productsToExclude,
    String paymentReference) {

	public BuyCartCommand {
		if (customerId == null) {
			throw new CartValidationException("customerId cannot be null");
		}
		if (productsToExclude == null) {
			productsToExclude = new ArrayList<>();
		}
	}

}
