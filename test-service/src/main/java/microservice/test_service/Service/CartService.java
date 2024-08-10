package microservice.test_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CartService {
    CompletableFuture<Result<Void>> createCart(Long clientId);
    CompletableFuture<Optional<CartDTO>> getCartByClientId(Long clientId);
}
