package microservice.cart_service.app.cart.core.domain.model;

import java.math.BigDecimal;

import lombok.Builder;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartItemId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartTimeStamps;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ItemPrice;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.Quantity;

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
