package microservice.cart_service.app.cart.core.application.command;

import microservice.cart_service.app.cart.core.domain.exception.CartValidationException;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;

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
