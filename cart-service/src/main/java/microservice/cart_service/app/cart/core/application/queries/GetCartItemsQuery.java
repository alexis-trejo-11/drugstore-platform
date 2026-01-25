package microservice.cart_service.app.cart.core.application.queries;

import org.springframework.data.domain.Pageable;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;

public record GetCartItemsQuery(CartId cartId, Pageable pageData) {
}
