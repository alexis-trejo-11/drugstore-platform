package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Mapper.CartItemMapper;
import microservice.ecommerce_cart_service.Mapper.CartMapper;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;


    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartMapper cartMapper, CartItemMapper cartItemMapper) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
    }

    @Override
    @Async
    @Transactional
    public Result<Void> createCart(Long clientId) {
        Optional<Cart> optionalCart = cartRepository.findByClientId(clientId);
        if (optionalCart.isPresent()) {
            return Result.error("Client Already Has a Cart");
        }

        Cart cart = new Cart(clientId);
        cartRepository.saveAndFlush(cart);

        return Result.success();
    }

    @Override
    @Async
    @Transactional
    public CartDTO getCartByClientId(Long clientId) {
        Optional<Cart> optionalCart = cartRepository.findByClientId(clientId);
        if (optionalCart.isEmpty()) {
            return null;
        }

        CartDTO cartDTO = makeCartDTO(optionalCart.get());

        return cartDTO;
    }

    private CartDTO makeCartDTO(Cart cart) {
        CartDTO cartDTO = cartMapper.entityToDTO(cart);

        List<CartItem> cartItems = cart.getCartItems();
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();

        for (CartItem cartItem : cartItems){
            CartItemDTO cartItemDTO = cartItemMapper.entityToDTO(cartItem);
            cartItemDTOS.add(cartItemDTO);
        }
        cartDTO.setCartItems(cartItemDTOS);

        return cartDTO;
    }
}
