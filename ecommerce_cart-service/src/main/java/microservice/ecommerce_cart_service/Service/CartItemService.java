package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;


public interface CartItemService {
    Result<Void> addProductsCart(Long clientId, Long productId, int quantity);
    Result<Void> deleteProductFromCart(Long clientId, CartItemInsertDTO cartItemInsertDTO);
    Result<CartDTO> processCartAndGetItems(ClientEcommerceDataDTO clientEcommerceDataDTO);
}
