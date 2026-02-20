package io.github.alexisTrejo11.drugstore.carts.cart.core.port.in.usecase;

import io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries.SearchCartsQuery;
import org.springframework.data.domain.Page;

import io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries.GetCartByCustomerIdQuery;
import io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries.GetCartByIdQuery;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.Cart;

public interface CartQueryUseCase {
  Cart getCartById(GetCartByIdQuery query);

  Cart getCartByCustomerId(GetCartByCustomerIdQuery query);

  Page<Cart> searchCarts(SearchCartsQuery query);
}
