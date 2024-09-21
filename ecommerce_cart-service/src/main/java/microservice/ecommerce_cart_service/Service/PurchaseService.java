package microservice.ecommerce_cart_service.Service;


import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.concurrent.CompletableFuture;


public interface PurchaseService {
    CompletableFuture<Result<ClientEcommerceDataDTO>> prepareClientData(Long clientId);
    void processPurchase(ClientEcommerceDataDTO clientEcommerceDataDTO, Long cardId, Long addressId);
}
