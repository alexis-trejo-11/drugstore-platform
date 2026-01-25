package microservice.cart_service.app.cart.core.application.command;

import java.util.Map;
import java.util.Set;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;

public record UpdateCartCommand(CustomerId customerId, Map<ProductId, Integer> productQuantitiesMap) {
  public UpdateCartCommand {
    UpdateCartCommandValidator.validateAll(customerId, productQuantitiesMap);
  }

  public Set<ProductId> productIds() {
    return productQuantitiesMap.keySet();
  }

}
