package io.github.alexisTrejo11.drugstore.carts.cart.core.application.command;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartValidationException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;

import java.util.List;


public record CreateAfterwardsCommand(List<ProductId> productIds, CustomerId customerId) {
  public CreateAfterwardsCommand {
    if (productIds.isEmpty()) {
      throw new CartValidationException("Product IDs list cannot be empty");
    }

    if (customerId != null && customerId.value() == null) {
      throw new CartValidationException("Cart ID cannot be null");
    }
  }

  public static CreateAfterwardsCommand from(String customerIdStr, List<String> productIdsStr) {
    var customerId = CustomerId.from(customerIdStr);
    List<ProductId> productIds = productIdsStr.stream()
        .map(ProductId::from)
        .toList();

    return new CreateAfterwardsCommand(productIds, customerId);
  }
}
