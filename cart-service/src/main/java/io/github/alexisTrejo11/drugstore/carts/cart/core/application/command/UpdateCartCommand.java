package io.github.alexisTrejo11.drugstore.carts.cart.core.application.command;

import java.util.Map;
import java.util.Set;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;

public record UpdateCartCommand(CustomerId customerId, Map<ProductId, Integer> productQuantitiesMap) {
  public UpdateCartCommand {
    UpdateCartCommandValidator.validateAll(customerId, productQuantitiesMap);
  }

  public Set<ProductId> productIds() {
    return productQuantitiesMap.keySet();
  }

}
