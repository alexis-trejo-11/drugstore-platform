package microservice.cart_service.app.cart.adapter.in.web.dto.input;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.cart_service.app.cart.core.application.command.RemoveAfterwardsCommand;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAfterwardsRequest {
  @NotNull(message = "Cart ID cannot be null")
  private String cartId;

  @NotNull(message = "Product IDs cannot be null")
  private Set<String> productIdSet;

  public RemoveAfterwardsCommand toCommand() {
    return RemoveAfterwardsCommand.from(cartId, productIdSet.stream().toList());
  }
}
