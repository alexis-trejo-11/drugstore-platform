package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.carts.cart.core.application.command.CreateAfterwardsCommand;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAfterwardsRequest {

  @NotNull(message = "Product IDs cannot be null")
  private Set<String> productIds;

  public CreateAfterwardsCommand toCommand(String userId) {
    return CreateAfterwardsCommand.from(userId, productIds.stream().toList());
  }
}
