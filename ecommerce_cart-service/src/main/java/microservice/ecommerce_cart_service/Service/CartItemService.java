package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.*;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.concurrent.CompletableFuture;


public interface CartItemService {
    CompletableFuture<Result<Void>> addProductsCart(Long clientId, Long productId, int quantity);
    CompletableFuture<Result<?>> deleteProductFromCart(Long clientId, CartItemInsertDTO cartItemInsertDTO);
    CompletableFuture<CartDTO> processCartAndGePurchaseData(ClientEcommerceDataDTO clientEcommerceDataDTO, PurchaseFromCartDTO purchaseFromCartDTO);
}
