package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.*;
import at.backend.drugstore.microservice.common_models.Utils.Result;


public interface CartItemService {
    Result<Void> addProductsCart(Long clientId, Long productId, int quantity);
    Result<Void> deleteProductFromCart(Long clientId, CartItemInsertDTO cartItemInsertDTO);
    CartDTO processCartAndGePurchaseData(ClientEcommerceDataDTO clientEcommerceDataDTO, PurchaseFromCartDTO purchaseFromCartDTO);
}
