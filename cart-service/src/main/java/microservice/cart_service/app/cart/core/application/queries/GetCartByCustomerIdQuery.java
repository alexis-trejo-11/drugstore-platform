package microservice.cart_service.app.cart.core.application.queries;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;

public record GetCartByCustomerIdQuery(CustomerId customerId) {

	public static GetCartByCustomerIdQuery from(String customerIdStr) {
		CustomerId customerId = CustomerId.from(customerIdStr);
		return new GetCartByCustomerIdQuery(customerId);
	}
}
