package microservice.test_service.Service.FacadeService;

import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.test_service.Utils.AddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class PurchaseServiceFacadeImpl implements PurchaseServiceFacade {


    private final ExternalServiceFacade externalServiceFacade;
    private final AddressValidator addressValidator;

    @Autowired
    public PurchaseServiceFacadeImpl(ExternalServiceFacade externalServiceFacade,
                                     AddressValidator addressValidator) {
        this.externalServiceFacade = externalServiceFacade;
        this.addressValidator = addressValidator;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<ClientEcommerceDataDTO>> prepareClientData(Long clientId) {
        // Call the aggregateClientData method asynchronously and return the result
        return externalServiceFacade.aggregateClientData(clientId);
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> processPurchase(ClientEcommerceDataDTO clientEcommerceDataDTO, Long cardId, CartDTO cartDTO, Long addressId) {
        // Validate address and create order asynchronously
        return CompletableFuture.supplyAsync(() -> {
            Result<Void> addressValidationResult = addressValidator.validateAddress(
                    clientEcommerceDataDTO.getClientDTO(),
                    clientEcommerceDataDTO.getAddressDTOS().stream()
                            .filter(address -> address.getId().equals(addressId))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Address not found"))
            );

            if (!addressValidationResult.isSuccess()) {
                throw new RuntimeException(addressValidationResult.getErrorMessage());
            }

            // Create order and return the CompletableFuture
            return externalServiceFacade.createOrder(cartDTO, clientEcommerceDataDTO.getClientDTO(), addressId);
        }).thenCompose(orderFuture ->
                orderFuture.thenCompose(orderId ->
                        // Process payment asynchronously
                        externalServiceFacade.processPayment(clientEcommerceDataDTO, cardId, orderId)
                )
        );
    }
}


