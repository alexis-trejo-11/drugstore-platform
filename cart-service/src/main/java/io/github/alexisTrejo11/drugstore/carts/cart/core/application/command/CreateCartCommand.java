package io.github.alexisTrejo11.drugstore.carts.cart.core.application.command;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartValidationException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.CreateCartParams;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;

public record CreateCartCommand(CustomerId customerId) {

  public CreateCartCommand {
    if (customerId == null || customerId.value() == null) {
      throw new CartValidationException("CustomerId cannot be null");
    }
  }

  public static CreateCartCommand from(String customerId) {
    return new CreateCartCommand(CustomerId.from(customerId));
  }

  public CreateCartParams toCreateCartParams() {
    return new CreateCartParams(customerId);
  }
}
