package microservice.cart_service.app.cart.adapter.input.web.dto.input;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import microservice.cart_service.app.cart.core.application.command.UpdateCartCommand;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;

public record UpdateCartRequest(
    @Valid @NotEmpty @Size(max = 50) Set<CartItemRequest> items) {

  public UpdateCartCommand toCommand(String customerId) {
    Map<ProductId, Integer> items = this.items.stream()
        .collect(Collectors.toMap(
            item -> new ProductId(item.productId()),
            CartItemRequest::quantity));

    var customerIdVO = new CustomerId(customerId);
    return new UpdateCartCommand(customerIdVO, items);
  }
}
