package io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;

public record GetCartByCustomerIdQuery(CustomerId customerId) {

	public static GetCartByCustomerIdQuery from(String customerIdStr) {
		CustomerId customerId = CustomerId.from(customerIdStr);
		return new GetCartByCustomerIdQuery(customerId);
	}
}
