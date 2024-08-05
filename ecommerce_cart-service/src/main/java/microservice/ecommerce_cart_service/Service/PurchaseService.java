package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.concurrent.CompletableFuture;


public interface PurchaseService {
    CompletableFuture<Result<ClientEcommerceDataDTO>> prepareClientData(Long clientId);
    CompletableFuture<Void>  processPurchase(ClientEcommerceDataDTO clientEcommerceDataDTO, Long cardId, CartDTO cartDTO, Long addressId);
}
