package microservice.cart_service.app.cart.adapter.in.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import microservice.cart_service.app.cart.adapter.in.web.dto.output.CartResponse;
import microservice.cart_service.app.cart.adapter.in.web.mapper.CartControllerMaper;
import microservice.cart_service.app.cart.core.application.queries.GetCartByCustomerIdQuery;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.port.in.usecase.CartQueryUseCase;
import microservice.cart_service.app.shared.ResponseWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/api/carts/admin")
public class CartAdminController {

  private CartQueryUseCase cartQueryUseCase;
  private CartControllerMaper mapper;

  @Autowired
  public CartAdminController(CartQueryUseCase cartQueryUseCase, CartControllerMaper mapper) {
    this.cartQueryUseCase = cartQueryUseCase;
    this.mapper = mapper;
  }

  @GetMapping("/{customerId}")
  private ResponseWrapper<CartResponse> getCustomerCart(@PathVariable @Valid @NotBlank String customerId) {
    GetCartByCustomerIdQuery query = GetCartByCustomerIdQuery.from(customerId);
    Cart cart = cartQueryUseCase.getCartByCustomerId(query);

    CartResponse cartResponse = mapper.entityToResponse(cart);
    return ResponseWrapper.found(cartResponse, "Customer Cart");
  }

  @GetMapping("/search")
  private ResponseWrapper<Void> searchCarts() {
    // Implementation for searching carts can be added here

    cartQueryUseCase.searchCarts();
    return ResponseWrapper.found(null, "Search Carts - Not Implemented");
  }
}
