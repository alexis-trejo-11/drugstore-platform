package io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model;

import java.math.BigDecimal;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartItemId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartTimeStamps;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ItemPrice;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.Quantity;

/**
 * Parameters record for reconstructing an existing CartItem from persistence.
 * Contains all fields needed to fully reconstruct a cart item.
 */
@Builder
public record ReconstructCartItemParams(
    CartItemId id,
    CartId cartId,
    ProductId productId,
    String productName,
    ItemPrice unitPrice,
    Quantity quantity,
    BigDecimal discountPerUnit,
    CartTimeStamps timeStamps) {
}
