package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.input;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.alexisTrejo11.drugstore.carts.cart.core.application.command.BuyCartCommand;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartValidationException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;

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
      throw new CartValidationException("CustomerId cannot be null");
    }

    var customerIdObj = new CustomerId(customerId);

    return new BuyCartCommand(customerIdObj, productsIdsToExcludeSet, null);
  }
}
