package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.Optional;

public interface CartService {
    Result<Void> createCart(Long clientId);
    Optional<CartDTO> getCartByClientId(Long clientId);
}
