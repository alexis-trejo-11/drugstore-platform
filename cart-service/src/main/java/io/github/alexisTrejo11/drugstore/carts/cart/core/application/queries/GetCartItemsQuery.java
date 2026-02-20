package io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries;

import org.springframework.data.domain.Pageable;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartId;

public record GetCartItemsQuery(CartId cartId, Pageable pageData) {
}
