package microservice.ecommerce_cart_service.app.infrastructure.port.in.web.controller;

import microservice.ecommerce_cart_service.app.application.dto.CartSummary;
import microservice.ecommerce_cart_service.app.shared.ResponseWrapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartAdminController {

    @GetMapping("/")
    private ResponseWrapper<CartSummary> getUserCart() {
        return null;
    }
}
