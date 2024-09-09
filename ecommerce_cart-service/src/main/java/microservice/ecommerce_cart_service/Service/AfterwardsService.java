package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AfterwardsService {
    Result<Void> moveProductToAfterwards(Long clientId, Long productId);
    Result<Void> returnProductToCart(Long clientId, Long productId);
    List<CartItemDTO> getAfterwardsByClientId(Long clientId);
    Optional<CartItemDTO> getAfterwardsBytId(Long afterwardsId);
}
