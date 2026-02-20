package io.github.alexisTrejo11.drugstore.carts.cart.core.application.command;



import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartValidationException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;

import java.util.List;


public record RemoveAfterwardsCommand(CartId cartId, List<ProductId> productIds) {
  public RemoveAfterwardsCommand {
    if (productIds.isEmpty()) {
      throw new CartValidationException("Product IDs list cannot be empty");
    }

    if (cartId != null && cartId.value() == null) {
      throw new CartValidationException("Cart ID cannot be null");
    }
  }

  public static RemoveAfterwardsCommand from(String cartIdStr, List<String> productIdsStr) {
    CartId cartId = CartId.from(cartIdStr);
    List<ProductId> productIds = productIdsStr.stream()
        .map(ProductId::from)
        .toList();

    return new RemoveAfterwardsCommand(cartId, productIds);
  }
}
