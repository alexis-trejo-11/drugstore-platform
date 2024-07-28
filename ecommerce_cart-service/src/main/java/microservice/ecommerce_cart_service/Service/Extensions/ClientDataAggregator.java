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

@Component
public class ClientDataAggregator {
    private final ExternalClientService externalClientService;
    private final ExternalAddressService externalAddressService;
    private final ExternalPaymentService externalPaymentService;
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public ClientDataAggregator(ExternalClientService externalClientService,
                                ExternalAddressService externalAddressService,
                                ExternalPaymentService externalPaymentService,
                                CartService cartService, CartItemService cartItemService, CartItemRepository cartItemRepository) {
        this.externalClientService = externalClientService;
        this.externalAddressService = externalAddressService;
        this.externalPaymentService = externalPaymentService;
        this.cartService = cartService;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    @Async
    public Result<ClientEcommerceDataDTO> aggregateClientData(Long clientId) {
        ClientEcommerceDataDTO clientEcommerceDataDTO = new ClientEcommerceDataDTO();

        Result<ClientDTO> clientResult = externalClientService.findClientById(clientId);
        if (!clientResult.isSuccess()) {
            return Result.error("Can't retrieve Client data");
        }
        clientEcommerceDataDTO.setClientDTO(clientResult.getData());

        Result<List<AddressDTO>> addressResult = externalAddressService.getAddressByClientId(clientId);
        if (!addressResult.isSuccess()) {
            return Result.error("Can't retrieve Addresses");
        }
        clientEcommerceDataDTO.setAddressDTOS(addressResult.getData());

        Result<List<CardDTO>> cardResult = externalPaymentService.getCardByClientId(clientId);
        if (!cardResult.isSuccess()) {
            return Result.error("Can't retrieve Payment data");
        }
        clientEcommerceDataDTO.setCardDTOS(cardResult.getData());

        Optional<CartDTO> cartResult = cartService.getCartByClientId(clientId);
        if (cartResult.isEmpty()) {
            return Result.error("Can't retrieve Cart data");
        }
        clientEcommerceDataDTO.setCartDTO(cartResult.get());

        return Result.success(clientEcommerceDataDTO);
    }
}