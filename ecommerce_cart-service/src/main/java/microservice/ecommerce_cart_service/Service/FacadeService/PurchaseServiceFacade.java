package microservice.ecommerce_cart_service.Service.FacadeService;

import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.concurrent.CompletableFuture;


public interface PurchaseServiceFacade {
    CompletableFuture<Result<ClientEcommerceDataDTO>> prepareClientData(Long clientId);
    CompletableFuture<Void>  processPurchase(ClientEcommerceDataDTO clientEcommerceDataDTO, Long cardId, CartDTO cartDTO, Long addressId);
}
