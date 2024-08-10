package microservice.test_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import microservice.test_service.Mappers.CartDtoMapper;
import microservice.test_service.Model.Cart;
import microservice.test_service.Repository.CartRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    @Async("taskExecutor")
    public CompletableFuture<Result<Void>> createCart(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Cart> optionalCart = cartRepository.findByClientId(clientId);
            if (optionalCart.isPresent()) {
                return Result.error("Client Already Has a Cart");
            }

            Cart cart = new Cart(clientId);
            cartRepository.saveAndFlush(cart);

            return Result.success();
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Optional<CartDTO>> getCartByClientId(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Cart> optionalCart = cartRepository.findByClientId(clientId);
            optionalCart.ifPresent(cart -> Hibernate.initialize(cart.getCartItems()));
            return optionalCart.map(cartDtoMapper::createCartDTO);
        });
    }
}
