package microservice.cart_service.app.cart.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotNull;

import microservice.cart_service.app.cart.core.application.command.RemoveAfterwardsCommand;

import java.util.Set;

public record DeleteAfterwardsRequest(
		@NotNull(message = "Product IDs cannot be null")
		Set<String> productIds) {

	public RemoveAfterwardsCommand toCommand(String customerId) {
		return RemoveAfterwardsCommand.from(customerId, productIds.stream().toList());
	}
}
