package microservice.ecommerce_cart_service.Service.Extensions;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Adress.ExternalAddressService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.ExternalService.Payment.ExternalPaymentService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Repository.CartItemRepository;
import microservice.ecommerce_cart_service.Service.CartItemService;
import microservice.ecommerce_cart_service.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class ClientDataAggregator {
    private final ExternalClientService externalClientService;
    private final ExternalAddressService externalAddressService;
    private final ExternalPaymentService externalPaymentService;
    private final CartService cartService;

    @Autowired
    public ClientDataAggregator(ExternalClientService externalClientService,
                                ExternalAddressService externalAddressService,
                                ExternalPaymentService externalPaymentService,
                                CartService cartService) {
        this.externalClientService = externalClientService;
        this.externalAddressService = externalAddressService;
        this.externalPaymentService = externalPaymentService;
        this.cartService = cartService;
    }

    @Transactional
    @Async("taskExecutor")
    public CompletableFuture<Result<ClientEcommerceDataDTO>> aggregateClientData(Long clientId) {
        // Start fetching data asynchronously
        CompletableFuture<Result<ClientDTO>> clientFuture = externalClientService.findClientById(clientId);

        CompletableFuture<Result<List<AddressDTO>>> addressFuture = externalAddressService.getAddressByClientId(clientId);

        CompletableFuture<Result<List<CardDTO>>> cardFuture = externalPaymentService.getCardByClientId(clientId);

        CompletableFuture<Optional<CartDTO>> cartFuture = cartService.getCartByClientId(clientId);

        // Combine all futures
        return CompletableFuture.allOf(clientFuture, addressFuture, cardFuture, cartFuture)
                .thenCompose(v -> {
                        // Check all results and map them
                        Result<ClientDTO> clientResult = clientFuture.join();
                        if (!clientResult.isSuccess()) {
                            return CompletableFuture.completedFuture(Result.error("Can't retrieve Client data"));
                        }

                        Optional<CartDTO> cartResult = cartFuture.join();
                        if (cartResult.isEmpty()) {
                            return CompletableFuture.completedFuture(Result.error("Can't retrieve Cart data"));
                        } else if (cartResult.get().getCartItems().isEmpty()) {
                            return CompletableFuture.completedFuture(Result.error("Cart has no products"));
                        }

                        Result<List<AddressDTO>> addressResult = addressFuture.join();
                        if (!addressResult.isSuccess()) {
                            return CompletableFuture.completedFuture(Result.error("Can't retrieve Addresses"));
                        }

                        Result<List<CardDTO>> cardResult = cardFuture.join();
                        if (!cardResult.isSuccess()) {
                            return CompletableFuture.completedFuture(Result.error("Can't retrieve Payment data"));
                        }

                        // Create and populate the result DTO
                        ClientEcommerceDataDTO clientEcommerceDataDTO = new ClientEcommerceDataDTO();
                        clientEcommerceDataDTO.setClientDTO(clientResult.getData());
                        clientEcommerceDataDTO.setAddressDTOS(addressResult.getData());
                        clientEcommerceDataDTO.setCardDTOS(cardResult.getData());
                        clientEcommerceDataDTO.setCartDTO(cartResult.get());


                        return CompletableFuture.completedFuture(Result.success(clientEcommerceDataDTO));
                });
    }

}