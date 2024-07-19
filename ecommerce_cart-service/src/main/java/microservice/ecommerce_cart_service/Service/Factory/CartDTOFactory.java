package microservice.ecommerce_cart_service.Service.Factory;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import microservice.ecommerce_cart_service.Mappers.CartItemMapper;
import microservice.ecommerce_cart_service.Mappers.CartMapper;
import microservice.ecommerce_cart_service.Model.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartDTOFactory {
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @Autowired
    public CartDTOFactory(CartMapper cartMapper, CartItemMapper cartItemMapper) {
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
    }

    public CartDTO createCartDTO(Cart cart) {
        CartDTO cartDTO = cartMapper.entityToDTO(cart);
        List<CartItemDTO> cartItemDTOS = cart.getCartItems().stream()
                .map(cartItemMapper::entityToDTO)
                .collect(Collectors.toList());
        cartDTO.setCartItems(cartItemDTOS);
        return cartDTO;
    }
}

