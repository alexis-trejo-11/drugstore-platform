package microservice.cart_service.app.cart.adapter.in.web.controller;

import microservice.cart_service.app.cart.adapter.in.web.dto.output.CartResponse;
import microservice.cart_service.app.shared.ResponseWrapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartAdminController {

  @GetMapping("/")
  private ResponseWrapper<CartResponse> getUserCart() {
    return null;
  }
}
