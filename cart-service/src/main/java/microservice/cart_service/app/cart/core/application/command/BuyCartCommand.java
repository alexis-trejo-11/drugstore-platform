package microservice.cart_service.app.cart.core.application.command;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import microservice.cart_service.app.cart.core.domain.validation.CartValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record BuyCartCommand(
    CustomerId customerId,
    List<ProductId> productsToExclude,
    String paymentReference) {

	public BuyCartCommand {
		if (customerId == null) {
			throw new IllegalArgumentException("customerId cannot be null");
		}
		if (productsToExclude == null) {
			productsToExclude = new ArrayList<>();
		}
	}

}
