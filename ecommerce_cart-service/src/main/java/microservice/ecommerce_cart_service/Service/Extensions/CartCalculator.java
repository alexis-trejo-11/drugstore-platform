package microservice.ecommerce_cart_service.Service.Extensions;

import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class CartCalculator {

    public Cart calculateCartNumbers(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }

        calculateSubTotal(cart);
        calculateItemsSubtotal(cart);

        return cart;
    }

    private void calculateSubTotal(Cart cart) {
        BigDecimal subtotal = cart.getCartItems().stream()
                .map(CartItem::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        cart.setSubtotal(subtotal);
    }

    private void calculateItemsSubtotal(Cart cart) {
        List<CartItem> cartItems = cart.getCartItems();
        for (CartItem cartItem : cartItems) {
            BigDecimal productPrice = cartItem.getProductPrice();
            int quantity = cartItem.getQuantity();
            BigDecimal itemTotal = productPrice.multiply(BigDecimal.valueOf(quantity))
                    .setScale(2, RoundingMode.HALF_UP);
            cartItem.setItemTotal(itemTotal);
        }
    }
}