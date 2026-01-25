package microservice.cart_service.app.cart.core.application.queries;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;

public record GetCartByIdQuery(CartId cartId, GetCartItemsQuery itemsQuery, boolean includeItems, boolean includeAfterwards) {
}
