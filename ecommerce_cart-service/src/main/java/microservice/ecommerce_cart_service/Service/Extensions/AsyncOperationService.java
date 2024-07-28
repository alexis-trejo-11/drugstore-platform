package microservice.ecommerce_cart_service.Service.Extensions;

import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

// AsyncOperationService.java
@Service
public class AsyncOperationService {
    private final CartRepository cartRepository;
    private final CartCalculator cartCalculator;

    @Autowired
    public AsyncOperationService(CartRepository cartRepository, CartCalculator cartCalculator) {
        this.cartRepository = cartRepository;
        this.cartCalculator = cartCalculator;
    }

    @Async
    public void calculateCartNumbers(Long clientId) {
        Optional<Cart> optionalCart = cartRepository.findByClientId(clientId);
        optionalCart.ifPresent(cart -> {
            cartCalculator.calculateCartNumbers(cart);
            cartRepository.save(cart);
        });
    }
}
