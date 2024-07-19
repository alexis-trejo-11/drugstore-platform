package microservice.ecommerce_cart_service.Service.Implementations;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Adress.ExternalAddressService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.ExternalService.Order.ExternalOrderService;
import at.backend.drugstore.microservice.common_models.ExternalService.Payment.ExternalPaymentService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final CartServiceImpl cartServiceImpl;
    private final ExternalClientService externalClientService;
    private final ExternalAddressService externalAddressService;
    private final ExternalPaymentService externalPaymentService;
    private final ExternalOrderService externalOrderService;

    @Autowired
    public PurchaseServiceImpl(CartServiceImpl cartServiceImpl,
                               ExternalClientService externalClientService,
                               ExternalAddressService externalAddressService,
                               ExternalPaymentService externalPaymentService,
                               ExternalOrderService externalOrderService) {
        this.cartServiceImpl = cartServiceImpl;
        this.externalClientService = externalClientService;
        this.externalAddressService = externalAddressService;
        this.externalPaymentService = externalPaymentService;
        this.externalOrderService = externalOrderService;
    }

    @Override
    @Async
    public Result<ClientEcommerceDataDTO> prepareClientData(Long clientId) {
        Result<ClientDTO> clientDTOResult = externalClientService.findClientById(clientId);
        if (!clientDTOResult.isSuccess()) {
            return Result.error("Can't Bring Client");
        }

        Result<List<AddressDTO>> addressResult = externalAddressService.getAddressByClientId(clientId);
        if (!addressResult.isSuccess()) {
            return Result.error("Cant Bring Addresses");
        }
        ClientEcommerceDataDTO clientEcommerceDataDTO = new ClientEcommerceDataDTO();

        Result<List<CardDTO>> cardResult = externalPaymentService.getCardByClientId(clientId);
        if (!cardResult.isSuccess()) {
            return Result.error("Cant Bring Payment Data");
        }

        Optional<CartDTO> optionalCartDTO = cartServiceImpl.getCartByClientId(clientId);

        clientEcommerceDataDTO.setCartDTO(optionalCartDTO.get());
        clientEcommerceDataDTO.setCardDTOS(cardResult.getData());
        clientEcommerceDataDTO.setClientDTO(clientDTOResult.getData());
        clientEcommerceDataDTO.setAddressDTOS(addressResult.getData());

        return Result.success(clientEcommerceDataDTO);
    }

    @Override
    @Async
    public void processPurchase(ClientEcommerceDataDTO clientEcommerceDataDTO, Long cardId, CartDTO cartDTO, Long addressId) {
        Long orderId = createOrder(cartDTO, clientEcommerceDataDTO.clientDTO, addressId);
        initPayment(clientEcommerceDataDTO, cardId, orderId);
    }

    private void initPayment(ClientEcommerceDataDTO clientEcommerceDataDTO, Long cardId, Long orderId) {
        CartDTO cartDTO = clientEcommerceDataDTO.getCartDTO();
        ClientDTO clientDTO = clientEcommerceDataDTO.getClientDTO();
        List<CardDTO> cardDTOS = clientEcommerceDataDTO.getCardDTOS();

        CardDTO cardDTO = cardDTOS.stream().filter(car -> car.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Card Not Found"));

        PaymentInsertDTO paymentInsertDTO = new PaymentInsertDTO();
        paymentInsertDTO.setPaymentMethod("CARD");
        paymentInsertDTO.setSubtotal(cartDTO.getSubtotal());
        paymentInsertDTO.setTotal(cartDTO.getSubtotal());
        paymentInsertDTO.setDiscount(BigDecimal.ZERO);
        paymentInsertDTO.setClientId(clientDTO.getId());
        paymentInsertDTO.setCardId(cardDTO.getId());
        paymentInsertDTO.setOrderId(orderId);

        PaymentDTO paymentDTO = externalPaymentService.initPayment(paymentInsertDTO);
        externalOrderService.addPaymentIdByOrderId(paymentDTO.getId(), orderId);
    }

    private Long createOrder(CartDTO cartDTO, ClientDTO clientDTO, Long addressId) {
        OrderInsertDTO orderInsertDTO = makeOrderInsertDTO(cartDTO, clientDTO, addressId);
        return externalOrderService.createOrderAndGetId(orderInsertDTO);
    }

    private Result<Void> ValidateAddress(ClientDTO clientDTO, AddressDTO addressDTO) {
        var isAddressCorrect = clientDTO.getId().equals(addressDTO.getClientId());
        if(!isAddressCorrect) {
            return Result.error("Invalid Address");
        }

        return Result.success();
    }

    private OrderInsertDTO makeOrderInsertDTO(CartDTO cartDTO, ClientDTO clientDTO, Long addressId) {
        OrderInsertDTO orderInsertDTO = new OrderInsertDTO();
        orderInsertDTO.setClientId(clientDTO.getId());
        orderInsertDTO.setCartDTO(cartDTO);
        orderInsertDTO.setAddressId(addressId);

        return orderInsertDTO;
    }
}
