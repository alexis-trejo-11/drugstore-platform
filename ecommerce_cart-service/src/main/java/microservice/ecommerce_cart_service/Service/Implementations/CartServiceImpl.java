package microservice.ecommerce_cart_service.Service.Implementations;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import microservice.ecommerce_cart_service.Service.Factory.CartDTOFactory;
import microservice.ecommerce_cart_service.Service.CartService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartDTOFactory cartDTOFactory;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartDTOFactory cartDTOFactory) {
        this.cartRepository = cartRepository;
        this.cartDTOFactory = cartDTOFactory;
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
            return optionalCart.map(cartDTOFactory::createCartDTO);
        });
    }
}
