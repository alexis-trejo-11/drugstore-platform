package microservice.ecommerce_cart_service.Service.Factory;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CartItemFactory {
    public CartItem createCartItem(ProductDTO productDTO, int quantity, Cart cart) {
        CartItem cartItem = new CartItem();
        cartItem.setProductId(productDTO.getId());
        cartItem.setProductName(productDTO.getName());
        cartItem.setProductPrice(productDTO.getPrice());
        cartItem.setQuantity(quantity);
        cartItem.setItemTotal(productDTO.getPrice().multiply(new BigDecimal(quantity)));
        cartItem.setCart(cart);
        return cartItem;
    }
}
