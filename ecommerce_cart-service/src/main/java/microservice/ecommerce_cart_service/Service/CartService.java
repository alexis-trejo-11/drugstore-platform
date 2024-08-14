package microservice.ecommerce_cart_service.Service;


import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CartService {
    CompletableFuture<Result<Void>> createCart(Long clientId);
    CompletableFuture<Optional<CartDTO>> getCartByClientId(Long clientId);
}
