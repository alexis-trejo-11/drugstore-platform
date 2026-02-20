package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotNull;

import io.github.alexisTrejo11.drugstore.carts.cart.core.application.command.RemoveAfterwardsCommand;

import java.util.Set;

public record DeleteAfterwardsRequest(
		@NotNull(message = "Product IDs cannot be null")
		Set<String> productIds) {

	public RemoveAfterwardsCommand toCommand(String customerId) {
		return RemoveAfterwardsCommand.from(customerId, productIds.stream().toList());
	}
}
