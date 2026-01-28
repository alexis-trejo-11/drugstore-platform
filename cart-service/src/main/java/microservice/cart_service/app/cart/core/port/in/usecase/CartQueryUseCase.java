package microservice.cart_service.app.cart.core.port.in.usecase;

import microservice.cart_service.app.cart.core.application.queries.SearchCartsQuery;
import org.springframework.data.domain.Page;

import microservice.cart_service.app.cart.core.application.queries.GetCartByCustomerIdQuery;
import microservice.cart_service.app.cart.core.application.queries.GetCartByIdQuery;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import org.springframework.data.domain.Pageable;

public interface CartQueryUseCase {
  Cart getCartById(GetCartByIdQuery query);

  Cart getCartByCustomerId(GetCartByCustomerIdQuery query);

  Page<Cart> searchCarts(SearchCartsQuery query);
}
