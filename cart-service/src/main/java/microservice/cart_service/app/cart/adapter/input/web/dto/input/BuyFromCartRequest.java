package microservice.cart_service.app.cart.adapter.input.web.dto.input;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import microservice.cart_service.app.cart.core.application.command.BuyCartCommand;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;

public record BuyFromCartRequest(Set<String> productsIdsToExclude) {
  public BuyFromCartRequest {
    if (productsIdsToExclude == null) {
      productsIdsToExclude = Set.of();
    }

    productsIdsToExclude = productsIdsToExclude.stream()
        .filter(id -> id != null && !id.isBlank())
        .collect(Collectors.toSet());
  }

  public BuyCartCommand toCommand(String customerId) {
    List<ProductId> productsIdsToExcludeSet = productsIdsToExclude.stream()
        .map(ProductId::new)
        .toList();

    if (customerId == null) {
      throw new IllegalArgumentException("CustomerId cannot be null");
    }

    var customerIdObj = new CustomerId(customerId);

    return new BuyCartCommand(customerIdObj, productsIdsToExcludeSet, null);
  }
}
