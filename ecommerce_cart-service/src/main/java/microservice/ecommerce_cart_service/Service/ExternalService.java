package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Adress.ExternalAddressService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.ExternalService.Payment.ExternalPaymentService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExternalService {

    private final ExternalClientService externalClientService;
    private final ExternalAddressService externalAddressService;
    private final ExternalPaymentService externalPaymentService;

    @Autowired
    public ExternalService(ExternalClientService externalClientService, ExternalAddressService externalAddressService, ExternalPaymentService externalPaymentService) {
        this.externalClientService = externalClientService;
        this.externalAddressService = externalAddressService;
        this.externalPaymentService = externalPaymentService;
    }

    public Result<ClientEcommerceDataDTO> getExternalServiceDataById(Long clientId) {
            try {
                // Fetch ecommerce client data
               Result<ClientDTO> clientDTOResult = externalClientService.findClientById(clientId);
                if (!clientDTOResult.isSuccess()) {
                    return Result.error("Can't bring client");
                }

                // Fetch ecommerce address data
                Result<List<AddressDTO>> addressResult = externalAddressService.getAddressByClientId(clientId);
                if (!addressResult.isSuccess()) {
                    return Result.error("Cant Bring Addresses");
                }
                ClientEcommerceDataDTO clientEcommerceDataDTO = new ClientEcommerceDataDTO();

                // Fetch payment client data
                Result<List<CardDTO>> cardResult = externalPaymentService.getCardByClientId(clientId);
                if (!cardResult.isSuccess()) {
                    return Result.error("Cant Bring Addresses");
                }

                clientEcommerceDataDTO.setCardDTOS(cardResult.getData());
                clientEcommerceDataDTO.setClientDTO(clientDTOResult.getData());
                clientEcommerceDataDTO.setAddressDTOS(addressResult.getData());

                return Result.success(clientEcommerceDataDTO);
            } catch (Exception e) {
                // Log the exception (optional)
                e.printStackTrace();
                throw new RuntimeException();
            }
        }


        public void makePayment(ClientEcommerceDataDTO clientEcommerceDataDTO, Long cardId) {
        CartDTO cartDTO = clientEcommerceDataDTO.getCartDTO();
        ClientDTO clientDTO = clientEcommerceDataDTO.getClientDTO();
        List<CardDTO> cardDTOS = clientEcommerceDataDTO.getCardDTOS();

        CardDTO cardDTO = cardDTOS.stream().filter(car -> car.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Card Not Found"));

        PaymentInsertDTO paymentInsertDTO =  new PaymentInsertDTO();
            paymentInsertDTO.setPaymentMethod("CARD");
            paymentInsertDTO.setAmount(cartDTO.getTotalPrice());
            paymentInsertDTO.setClientId(clientDTO.getId());
            paymentInsertDTO.setCardId(cardDTO.getId());

            externalPaymentService.initPayment(paymentInsertDTO);
        }
    }
