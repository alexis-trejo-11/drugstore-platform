package microservice.cart_service.app.cart.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.cart_service.app.cart.core.application.command.CreateAfterwardsCommand;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAfterwardsRequest {
  @NotNull(message = "Cart ID cannot be null")
  private String cartId;

  @NotNull(message = "Product IDs cannot be null")
  private Set<String> productIds;

  public CreateAfterwardsCommand toCommand() {
    return CreateAfterwardsCommand.from(cartId, productIds.stream().toList());
  }
}
