package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

public interface CartService {
    Result<Void> createCart(Long clientId);
    CartDTO getCartByClientId(Long clientId);
}
