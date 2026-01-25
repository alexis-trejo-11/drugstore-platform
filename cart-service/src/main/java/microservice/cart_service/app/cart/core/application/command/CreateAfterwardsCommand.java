package microservice.cart_service.app.cart.core.application.command;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;

import java.util.List;


public record CreateAfterwardsCommand(List<ProductId> productIds, CartId cartId) {
  public CreateAfterwardsCommand {
    if (productIds.isEmpty()) {
      throw new IllegalArgumentException("Product IDs list cannot be empty");
    }

    if (cartId != null && cartId.value() == null) {
      throw new IllegalArgumentException("Cart ID cannot be null");
    }
  }

  public static CreateAfterwardsCommand from(String cartIdStr, List<String> productIdsStr) {
    CartId cartId = CartId.from(cartIdStr);
    List<ProductId> productIds = productIdsStr.stream()
        .map(ProductId::from)
        .toList();

    return new CreateAfterwardsCommand(productIds, cartId);
  }
}
