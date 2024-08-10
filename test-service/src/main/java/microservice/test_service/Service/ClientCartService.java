package microservice.test_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Cart.PurchaseFromCartDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.concurrent.CompletableFuture;


public interface ClientCartService {
    CompletableFuture<Result<Void>> addProductsCart(Long clientId, Long productId, int quantity);
    CompletableFuture<Result<?>> deleteProductFromCart(Long clientId, CartItemInsertDTO cartItemInsertDTO);
    CompletableFuture<CartDTO> processCartAndGePurchaseData(ClientEcommerceDataDTO clientEcommerceDataDTO, PurchaseFromCartDTO purchaseFromCartDTO);
}
