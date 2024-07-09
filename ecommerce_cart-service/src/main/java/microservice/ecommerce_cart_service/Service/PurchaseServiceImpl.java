package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Adress.ExternalAddressService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.ExternalService.Order.ExternalOrderService;
import at.backend.drugstore.microservice.common_models.ExternalService.Payment.ExternalPaymentService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final CartServiceImpl cartServiceImpl;
    private final ExternalClientService externalClientService;
    private final ExternalAddressService externalAddressService;
    private final ExternalPaymentService externalPaymentService;
    private final ExternalOrderService externalOrderService;

    @Autowired
    public PurchaseServiceImpl(CartServiceImpl cartServiceImpl, ExternalClientService externalClientService, ExternalAddressService externalAddressService, ExternalPaymentService externalPaymentService, ExternalOrderService externalOrderService) {
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

        CartDTO cartDTO = cartServiceImpl.getCartByClientId(clientId);

        clientEcommerceDataDTO.setCartDTO(cartDTO);
        clientEcommerceDataDTO.setCardDTOS(cardResult.getData());
        clientEcommerceDataDTO.setClientDTO(clientDTOResult.getData());
        clientEcommerceDataDTO.setAddressDTOS(addressResult.getData());

        return Result.success(clientEcommerceDataDTO);
    }

    @Override
    @Async
    public void processPurchase(ClientEcommerceDataDTO clientEcommerceDataDTO, Long cardId, List<CartItemDTO> itemsToPurchase, Long addressId) {
        createOrder(itemsToPurchase, clientEcommerceDataDTO, addressId);
        initPayment(clientEcommerceDataDTO, cardId);
    }

    private void initPayment(ClientEcommerceDataDTO clientEcommerceDataDTO, Long cardId) {
        CartDTO cartDTO = clientEcommerceDataDTO.getCartDTO();
        ClientDTO clientDTO = clientEcommerceDataDTO.getClientDTO();
        List<CardDTO> cardDTOS = clientEcommerceDataDTO.getCardDTOS();

        CardDTO cardDTO = cardDTOS.stream().filter(car -> car.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Card Not Found"));

        PaymentInsertDTO paymentInsertDTO = new PaymentInsertDTO();
        paymentInsertDTO.setPaymentMethod("CARD");
        paymentInsertDTO.setAmount(cartDTO.getTotalPrice());
        paymentInsertDTO.setClientId(clientDTO.getId());
        paymentInsertDTO.setCardId(cardDTO.getId());

        externalPaymentService.initPayment(paymentInsertDTO);
    }

    private void createOrder(List<CartItemDTO> itemsToPurchase, ClientEcommerceDataDTO clientEcommerceDataDTO, Long addressId) {
        List<AddressDTO> addressDTOS = clientEcommerceDataDTO.getAddressDTOS();
        ClientDTO clientDTO = clientEcommerceDataDTO.clientDTO;

        AddressDTO addressDTO = addressDTOS.stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Address with ID " + addressId + " not found"));

        OrderInsertDTO orderInsertDTO = makeOrderInsertDTO(itemsToPurchase, clientDTO);
        orderInsertDTO.setAddressDTO(addressDTO);

        externalOrderService.createOrder(orderInsertDTO);
    }

    private Result<Void> ValidateAddress(ClientDTO clientDTO, AddressDTO addressDTO) {
        var isAddressCorrect = clientDTO.getId().equals(addressDTO.getClientId());
        if(!isAddressCorrect) {
            return Result.error("Invalid Address");
        }

        return Result.success();
    }

    private OrderInsertDTO makeOrderInsertDTO(List<CartItemDTO> itemsToPurchase, ClientDTO clientDTO) {
            OrderInsertDTO orderInsertDTO = new OrderInsertDTO();
            orderInsertDTO.setClientDTO(clientDTO);

            List<OrderItemInsertDTO> orderItemInsertDTOS = new ArrayList<>();
            for (CartItemDTO item : itemsToPurchase) {
                OrderItemInsertDTO orderItemInsertDTO = new OrderItemInsertDTO();
                orderItemInsertDTO.setProductId(item.getProductId());
                orderItemInsertDTO.setQuantity(item.getQuantity());

                orderItemInsertDTOS.add(orderItemInsertDTO);
            }

            orderInsertDTO.setItems(orderItemInsertDTOS);

            return orderInsertDTO;
        }

}
