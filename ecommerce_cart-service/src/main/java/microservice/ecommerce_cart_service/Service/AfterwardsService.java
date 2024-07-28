package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Model.CartItem;

import java.util.List;
import java.util.Optional;

public interface AfterwardsService {
    Result<Void> moveProductToAfterwards(Long clientId, Long productId);
    void returnProductToCart(Long clientId, Long productId);
    List<CartItemDTO> getAfterwardsByClientId(Long clientId);
    Optional<CartItemDTO> getAfterwardsBytId(Long afterwardsId);
}
