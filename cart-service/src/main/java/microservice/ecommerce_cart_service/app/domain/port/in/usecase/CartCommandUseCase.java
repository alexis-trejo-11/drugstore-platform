package microservice.ecommerce_cart_service.app.domain.port.in.usecase;

import microservice.ecommerce_cart_service.app.application.command.cart.ClearCartCommand;
import microservice.ecommerce_cart_service.app.application.command.cart.CreateCartCommand;
import microservice.ecommerce_cart_service.app.application.command.cart_items.AddItemsToCartCommand;
import microservice.ecommerce_cart_service.app.application.command.cart_items.RemoveItemFromCartCommand;
import microservice.ecommerce_cart_service.app.application.command.cart_items.UpdateCartItemQuantityCommand;
import microservice.ecommerce_cart_service.app.domain.entities.Cart;


public interface CartCommandUseCase {
    Cart createCart(CreateCartCommand command);
    Cart addItemsToCart(AddItemsToCartCommand command);
    Cart removeItemFromCart(RemoveItemFromCartCommand command);
    Cart updateCartItemQuantity(UpdateCartItemQuantityCommand command);
    void clearCart(ClearCartCommand command);
}


