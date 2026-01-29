package microservice.cart_service.app.cart.core.application.command;

import microservice.cart_service.app.cart.core.domain.exception.CartValidationException;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;

public record ClearCartCommand(CustomerId customerId) {
	public ClearCartCommand {
		if (customerId == null) {
			throw new CartValidationException("CustomerId cannot be null");
		}
	}
}
