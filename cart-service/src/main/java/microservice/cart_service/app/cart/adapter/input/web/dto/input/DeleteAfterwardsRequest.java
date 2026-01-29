package microservice.cart_service.app.cart.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotNull;

import microservice.cart_service.app.cart.core.application.command.RemoveAfterwardsCommand;

import java.util.Set;

public record DeleteAfterwardsRequest(
		@NotNull(message = "Cart ID cannot be null")
		String cartId,
		@NotNull(message = "Product IDs cannot be null")
		Set<String> productIds) {

	public RemoveAfterwardsCommand toCommand() {
		return RemoveAfterwardsCommand.from(cartId, productIds.stream().toList());
	}
}
