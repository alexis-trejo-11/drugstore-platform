package microservice.cart_service.app.cart.core.application.command;

import microservice.cart_service.app.cart.core.domain.exception.CartValidationException;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;

public record DeleteCartCommand(CartId cartId) {
	public DeleteCartCommand {
		if (cartId == null) {
			throw new CartValidationException("CartId cannot be null");
		}
	}
}
