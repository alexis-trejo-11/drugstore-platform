package microservice.cart_service.app.cart.core.port.in.usecase;

import org.springframework.data.domain.Page;

import microservice.cart_service.app.cart.core.application.queries.GetCartByCustomerIdQuery;
import microservice.cart_service.app.cart.core.application.queries.GetCartByIdQuery;
import microservice.cart_service.app.cart.core.domain.model.Cart;

public interface CartQueryUseCase {
  Cart getCartById(GetCartByIdQuery query);

  Cart getCartByCustomerId(GetCartByCustomerIdQuery query);

  Page<Cart> searchCarts();
  // CartSummary getCartSummary(GetCartSummaryQuery query);
}
