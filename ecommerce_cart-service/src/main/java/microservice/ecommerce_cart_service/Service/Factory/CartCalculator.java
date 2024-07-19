package microservice.ecommerce_cart_service.Service.Factory;

import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartCalculator {
    public void calculateCartNumbers(Cart cart) {
       calculateSubTotal(cart);

       // If cart has items calcultate subtotal per item
       if (!cart.getCartItems().isEmpty()) {
           calculateItemsSubtotal(cart);
       }
    }

    private void calculateSubTotal(Cart cart) {
        BigDecimal subtotal = cart.getCartItems().stream()
                .map(CartItem::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setSubtotal(subtotal);
    }

    private void calculateItemsSubtotal(Cart cart) {
        List<CartItem> cartItems = cart.getCartItems();
        for (var cartItem : cartItems) {
            BigDecimal productPrice = cartItem.getProductPrice();
            int quantity = cartItem.getQuantity();
            cartItem.setItemTotal(productPrice.multiply(BigDecimal.valueOf(quantity)));
        }
    }
}
