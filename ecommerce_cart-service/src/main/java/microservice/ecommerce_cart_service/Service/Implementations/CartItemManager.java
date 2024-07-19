package microservice.ecommerce_cart_service.Service.Implementations;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import microservice.ecommerce_cart_service.Repository.CartItemRepository;
import microservice.ecommerce_cart_service.Service.Factory.CartItemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class CartItemManager {
    private final CartItemRepository cartItemRepository;
    private final CartItemFactory cartItemFactory;

    @Autowired
    public CartItemManager(CartItemRepository cartItemRepository, CartItemFactory cartItemFactory) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemFactory = cartItemFactory;
    }

    public CartItem addOrUpdateCartItem(Cart cart, ProductDTO productDTO, int quantity) {
        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(productDTO.getId()))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setItemTotal(cartItem.getProductPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            return cartItemRepository.save(cartItem);
        } else {
            CartItem newCartItem = cartItemFactory.createCartItem(productDTO, quantity, cart);
            return cartItemRepository.save(newCartItem);
        }
    }

    public void removeCartItem(Cart cart, Long productId, int quantity) {
        Optional<CartItem> optionalCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            if (cartItem.getQuantity() <= quantity) {
                cart.getCartItems().remove(cartItem);
                cartItemRepository.delete(cartItem);
            } else {
                cartItem.setQuantity(cartItem.getQuantity() - quantity);
                cartItem.setItemTotal(cartItem.getProductPrice().multiply(new BigDecimal(cartItem.getQuantity())));
                cartItemRepository.save(cartItem);
            }
        }
    }
}