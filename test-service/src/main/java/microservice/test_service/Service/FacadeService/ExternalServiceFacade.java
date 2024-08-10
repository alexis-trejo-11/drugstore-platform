package microservice.test_service.Service.FacadeService;

import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.concurrent.CompletableFuture;

public interface ExternalServiceFacade {
    CompletableFuture<Void> processPayment(ClientEcommerceDataDTO clientData, Long cardId, Long orderId);
    CompletableFuture<Result<ClientEcommerceDataDTO>> aggregateClientData(Long clientId);
    CompletableFuture<Long> createOrder(CartDTO cartDTO, ClientDTO clientDTO, Long addressId);
}
