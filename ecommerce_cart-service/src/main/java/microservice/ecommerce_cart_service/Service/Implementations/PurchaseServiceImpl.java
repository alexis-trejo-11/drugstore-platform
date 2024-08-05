package microservice.ecommerce_cart_service.Service.Implementations;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Service.Extensions.AddressValidator;
import microservice.ecommerce_cart_service.Service.Extensions.ClientDataAggregator;
import microservice.ecommerce_cart_service.Service.Extensions.OrderCreator;
import microservice.ecommerce_cart_service.Service.Extensions.PaymentProcessor;
import microservice.ecommerce_cart_service.Service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final ClientDataAggregator clientDataAggregator;
    private final PaymentProcessor paymentProcessor;
    private final OrderCreator orderCreator;
    private final AddressValidator addressValidator;

    @Autowired
    public PurchaseServiceImpl(ClientDataAggregator clientDataAggregator,
                               PaymentProcessor paymentProcessor,
                               OrderCreator orderCreator,
                               AddressValidator addressValidator) {
        this.clientDataAggregator = clientDataAggregator;
        this.paymentProcessor = paymentProcessor;
        this.orderCreator = orderCreator;
        this.addressValidator = addressValidator;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<ClientEcommerceDataDTO>> prepareClientData(Long clientId) {
        // Call the aggregateClientData method asynchronously and return the result
        return clientDataAggregator.aggregateClientData(clientId);
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
            return orderCreator.createOrder(cartDTO, clientEcommerceDataDTO.getClientDTO(), addressId);
        }).thenCompose(orderFuture ->
                orderFuture.thenCompose(orderId ->
                        // Process payment asynchronously
                        paymentProcessor.processPayment(clientEcommerceDataDTO, cardId, orderId)
                )
        );
    }
}


