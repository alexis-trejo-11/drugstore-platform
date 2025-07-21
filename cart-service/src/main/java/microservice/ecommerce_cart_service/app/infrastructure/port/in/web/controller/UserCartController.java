package microservice.ecommerce_cart_service.app.infrastructure.port.in.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import microservice.ecommerce_cart_service.app.application.command.cart_items.AddItemsToCartCommand;
import microservice.ecommerce_cart_service.app.application.command.cart_items.RemoveItemFromCartCommand;
import microservice.ecommerce_cart_service.app.application.queries.GetCartByCustomerIdQuery;
import microservice.ecommerce_cart_service.app.domain.entities.Cart;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CustomerId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.ProductId;
import microservice.ecommerce_cart_service.app.domain.port.in.usecase.CartCommandUseCase;
import microservice.ecommerce_cart_service.app.domain.port.in.usecase.CartQueryUseCase;
import microservice.ecommerce_cart_service.app.infrastructure.port.in.web.dto.BuyFromCartRequest;
import microservice.ecommerce_cart_service.app.infrastructure.port.in.web.dto.CartItemRequest;
import microservice.ecommerce_cart_service.app.application.dto.CartSummary;
import microservice.ecommerce_cart_service.app.infrastructure.port.in.web.dto.RemoveProductsRequest;
import microservice.ecommerce_cart_service.app.shared.ResponseWrapper;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/api/carts/users")
@RequiredArgsConstructor
public class UserCartController {
    private final CartCommandUseCase commandUseCase;
    private final CartQueryUseCase cartQueryUseCase;

    @GetMapping("/{userId}")
    private ResponseWrapper<CartSummary> getUserCart(@Valid @PathVariable UUID userId) {
        GetCartByCustomerIdQuery query = new GetCartByCustomerIdQuery(new CustomerId(userId));
        Cart cart = cartQueryUseCase.getCartByCustomerId(query);
        return ResponseWrapper.found(CartSummary.emptyCart(cart), "Cart");
    }

    @PostMapping("/{userId}/items")
    private ResponseWrapper<Void> addItemsToCart(@Valid @PathVariable UUID userId, @RequestBody Set<CartItemRequest> cartItemRequestSet) {
        var command = new AddItemsToCartCommand(new CustomerId(userId), cartItemRequestSet);
        commandUseCase.addItemsToCart(command);
        return ResponseWrapper.success("Products successfully added to cart");
    }

    @PostMapping("/{userId}/products")
    private ResponseWrapper<Void> removeProductsToCart(@Valid @PathVariable UUID userId, @RequestBody RemoveProductsRequest addItemsRequest) {
        var productIds = addItemsRequest.getProductUUIDs().stream()
                .map(ProductId::new)
                .collect(Collectors.toSet());

        var command = new RemoveItemFromCartCommand(new CustomerId(userId), productIds);
        commandUseCase.removeItemFromCart(command);

        return ResponseWrapper.success("Products successfully deleted emptyCart cart");
    }

    @PostMapping("/{userId}/buy")
    private ResponseWrapper<Void> buyFromCart(@Valid @PathVariable UUID userId, @RequestBody BuyFromCartRequest buyFromCartRequest) {
        return ResponseWrapper.success("Products successfully deleted emptyCart cart");
    }
}

