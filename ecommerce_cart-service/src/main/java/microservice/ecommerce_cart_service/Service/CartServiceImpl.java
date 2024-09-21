package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.ecommerce_cart_service.Mappers.CartDtoMapper;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartDtoMapper cartDtoMapper;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartDtoMapper cartDtoMapper) {
        this.cartRepository = cartRepository;
        this.cartDtoMapper = cartDtoMapper;
    }

    @Override
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
    public CartDTO getCartByClientId(Long clientId) {
            Cart cart = cartRepository.findByClientId(clientId).orElse(null);
            if (cart == null) { return null; }

            List<CartItem> cartItems = cart.getCartItems();
            return cartDtoMapper.createCartDTO(cart, cartItems);
    }
}
