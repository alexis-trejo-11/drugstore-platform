package io.github.alexisTrejo11.drugstore.carts.cart.core.application.command;

import java.util.List;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartValidationException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;

public record ClearCartCommand(CustomerId customerId, String reason, List<ProductId> excludeProductIds) {
  public ClearCartCommand {
    if (customerId == null) {
      throw new CartValidationException("CustomerId cannot be null");
    }
    if (reason == null || reason.isBlank()) {
      throw new CartValidationException("Reason cannot be null or blank");
    }
    if (excludeProductIds != null && excludeProductIds.contains(null)) {
      throw new CartValidationException("ExcludeProductIds cannot contain null values");
    }
    if (excludeProductIds != null && excludeProductIds.isEmpty()) {
      excludeProductIds = new java.util.ArrayList<>();
    }
  }
}
