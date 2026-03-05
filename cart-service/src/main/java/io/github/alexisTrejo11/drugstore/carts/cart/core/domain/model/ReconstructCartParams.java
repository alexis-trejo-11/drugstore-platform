package io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model;

import java.util.List;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartTimeStamps;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;
import lombok.Builder;

@Builder
public record ReconstructCartParams(
    CartId id,
    CustomerId customerId,
    List<CartItem> items,
    List<AfterwardsItem> afterwardsItems,
    CartTimeStamps timeStamps) {

  public static ReconstructCartParams withEmptyItems(
      CartId id,
      CustomerId customerId,
      CartTimeStamps timeStamps) {
    return new ReconstructCartParams(id, customerId, List.of(), List.of(), timeStamps);
  }
}
